package ebolo.libma.stub.utils.users;

import ebolo.libma.data.data.raw.user.Librarian;
import ebolo.libma.stub.db.DbPortal;

/**
 * A utility help adding new librarian profile into system
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class CreateNewUser {
    public static void main(String[] args) {
        DbPortal.getInstance().setUp();
        Librarian librarian = new Librarian(
            "Duy",
            "Thanh",
            "Dao",
            "ebolo@yolo.com",
            "ebolo",
            "eboloyolo5653",
            "5653"
        );
        DbPortal.getInstance().getUserDb().insertOne(librarian.toMongoDocument());
    }
}
