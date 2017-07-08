package ebolo.libma.data.db.local;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import ebolo.libma.data.data.raw.AbstractMongolizable;
import ebolo.libma.data.data.ui.ObjectUIWrapper;
import ebolo.libma.data.db.factories.MongolizableFactory;
import ebolo.libma.data.db.factories.ObjectUIWrapperFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.bson.Document;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.connection.ByteBufferBsonOutput.MAX_BUFFER_SIZE;

/**
 * This is the class that serves as the generic template for all the list manager classes. A list manager
 * basically would have a list of raw data resource as well as its relevant ui wrapper objects. And to manage
 * those resources, vast of management methods are included
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see AbstractMongolizable
 * @see ObjectUIWrapper
 * @see MongolizableFactory
 * @see ObjectUIWrapperFactory
 * @since 20/06/2017
 */

public abstract class ListManager<T extends AbstractMongolizable, S extends ObjectUIWrapper<T>> {
    /**
     * current database version, is used to synchronize with the online database
     */
    private long currentVersion = 0;
    /**
     * a map of object ID to raw data resource
     */
    private Map<String, T> mainList;
    /**
     * a list of relevant ui wrapper objects for raw data resource
     */
    private ObservableList<S> uiList;
    /**
     * a list that wraps the uiList and has filering capability
     */
    private FilteredList<S> uiWrapperFilteredList;
    /**
     * a list that wrap the filtered list with sorting capability
     */
    private SortedList<S> uiWrapperSortedList;
    /**
     * a string indicates the type of the list manager - solely for debugging and file naming purpose
     */
    private String type;
    private String databaseDirectoryPath;
    /**
     * the factory that produces T objects
     */
    private MongolizableFactory<T> tFactory;
    /**
     * the factory that produces S objects
     */
    private ObjectUIWrapperFactory<T, S> sFactory;
    
    ListManager() {
        mainList = new HashMap<>();
        uiList = FXCollections.observableArrayList();
        uiWrapperFilteredList = new FilteredList<>(
            uiList,
            objectUIWrapper -> true
        );
        uiWrapperSortedList = new SortedList<>(
            uiWrapperFilteredList
        );
    }
    
    public void add(T t, S s) {
        mainList.put(s.getObjectId(), t);
        Platform.runLater(() -> uiList.add(s));
        
        saveLocal();
    }
    
    public void delete(List<S> deletedObjectList) {
        deletedObjectList.forEach(objectUIWrapper -> mainList.remove(objectUIWrapper.getObjectId()));
        Platform.runLater(() -> uiList.removeAll(deletedObjectList));
        
        saveLocal();
    }
    
    public void deleteByObjectIds(List<String> objectIds) {
        List<S> test = uiList.filtered(s -> objectIds.contains(s.getObjectId()));
        delete(test);
    }
    
    /**
     * This function would help synchronize with the online (internal) database. Steps are described thoughtfully
     * inside the method itself
     *
     * @param retrievedUpdateList the list of prepared Bson document retrieved from the central stub
     * @author Ebolo
     */

    public void sync(List<Document> retrievedUpdateList) {
        // Pre-processing: trim down the list to eliminate obsoleted entries
        final List<Document> updateList = retrievedUpdateList
            .parallelStream()
            .filter(document -> document.getObjectId("_id") != null)
            .collect(Collectors.toList());
        Logger.getLogger("myApp").log(Level.INFO, type + " updates: " + updateList.size());
        // Step 1: make objects from the Bson documents and update directly to the mainList
        updateList.parallelStream()
            .forEach(document -> mainList.put(
                document.getObjectId("_id").toString(),
                tFactory.produce(document)
            ));
        
        // Step 2a: prepare the list of updated objects' object ID
        final List<String> updateIds = updateList
            .parallelStream()
            .map(document -> document.getObjectId("_id").toString())
            .collect(Collectors.toList());
        Platform.runLater(() -> {
            // Step 2b: remove all the obsoleted objects from current uiList
            uiList.removeIf(objectUIWrapper -> updateIds.contains(objectUIWrapper.getObjectId()));
            // Step 2c: add all the updated items to current uiList
            updateList.forEach(document ->
                uiList.add(sFactory.produce(tFactory.produce(document), document.getObjectId("_id").toString()))
            );
        });
        
        // Step 3: update currentVersion to the most recent one from database
        Optional<Long> last_modified = updateList
            .parallelStream()
            .map(document -> document.getLong("last_modified"))
            .max(Long::compare);
        last_modified.ifPresent(aLong -> currentVersion = aLong);
        
        // Step 4: post-work, log and save
        Logger.getLogger("myApp").log(Level.INFO, "Current " + type.toLowerCase() + " DB version: " + currentVersion);
        saveLocal();
    }
    
    public void modify(T t, S s) {
        t.setObjectId(s.getObjectId());
        mainList.put(s.getObjectId(), t);
        Platform.runLater(() -> s.update(t));
        
        saveLocal();
    }
    
    /**
     * If the local database file exists then we should read from it instead/before requesting a new
     * update from the online database. In addition, this function uses Kryo v4.0.0 serialization
     * framework to support the loading mechanism. More info can be found at their repository
     *
     * @author Ebolo
     * @see <a href="https://github.com/EsotericSoftware/kryo">Kryo repo</a>
     */
    
    @SuppressWarnings("unchecked")
    void loadLocal() {
        try {
            File dbFile = new File(databaseDirectoryPath + File.separatorChar + type + ".db");
            if (dbFile.exists()) {
                Kryo kryo = new Kryo();
                ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
                UnsafeInput unsafeInput = new UnsafeInput(
                    new FileInputStream(new RandomAccessFile(dbFile, "rw").getFD()),
                    MAX_BUFFER_SIZE
                );
                currentVersion = unsafeInput.readLong();
                mainList = (HashMap<String, T>) kryo.readClassAndObject(unsafeInput);
                unsafeInput.close();
                
                uiList.clear();
                uiList.addAll(
                    mainList.values()
                        .parallelStream()
                        .map(t -> sFactory.produce(t, t.getObjectId())).collect(Collectors.toList())
                );
            }
        } catch (Exception e) {
            syncStub();
        }
    }
    
    /**
     * The local database file would be updated whenever there is a new update either to online or
     * local database. In addition, this function uses Kryo v4.0.0 serialization framework to support
     * the saving mechanism. More info can be found at their repository
     *
     * @author Ebolo
     * @see <a href="https://github.com/EsotericSoftware/kryo">Kryo repo</a>
     */
    
    private void saveLocal() {
        try {
            File dbFile = new File(databaseDirectoryPath + File.separatorChar + type + ".db");
            if (!dbFile.exists())
                dbFile.createNewFile();
            Kryo kryo = new Kryo();
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            UnsafeOutput unsafeOutput = new UnsafeOutput(
                new FileOutputStream(new RandomAccessFile(dbFile, "rw").getFD()),
                MAX_BUFFER_SIZE
            );
            unsafeOutput.writeLong(currentVersion);
            kryo.writeClassAndObject(unsafeOutput, mainList);
            unsafeOutput.flush();
            unsafeOutput.close();
        } catch (IOException ignored) {
        }
    }
    
    /**
     * This function would help set the necessary fields for the list manager to work properly
     *
     * @param type                  {@link #type}
     * @param databaseDirectoryPath {@link #databaseDirectoryPath}
     * @param tFactory              {@link #tFactory}
     * @param sFactory              {@link #sFactory}
     * @author Ebolo
     */
    
    public void setInfo(String type, String databaseDirectoryPath, MongolizableFactory<T> tFactory,
                        ObjectUIWrapperFactory<T, S> sFactory) {
        this.type = type;
        this.databaseDirectoryPath = databaseDirectoryPath;
        this.tFactory = tFactory;
        this.sFactory = sFactory;
    }
    
    long getCurrentVersion() {
        return currentVersion;
    }
    
    public ObservableList<S> getUiList() {
        return uiList;
    }
    
    public FilteredList<S> getUiWrapperFilteredList() {
        return uiWrapperFilteredList;
    }
    
    public SortedList<S> getUiWrapperSortedList() {
        return uiWrapperSortedList;
    }
    
    public String getType() {
        return type;
    }
    
    public abstract void syncStub();
}
