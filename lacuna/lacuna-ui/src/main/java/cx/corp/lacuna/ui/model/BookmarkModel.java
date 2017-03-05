package cx.corp.lacuna.ui.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class BookmarkModel {

    private final Set<Bookmark> bookmarks = new HashSet<>();
    private final BookmarkPersistence persistence;
    private boolean unsavedChanges = false;

    public BookmarkModel(BookmarkPersistence persistence) {
        this.persistence = persistence;
    }

    public boolean addBookmark(Bookmark bookmark) {
        unsavedChanges = true;
        return bookmarks.add(bookmark);
    }

    public boolean removeBookmark(Bookmark bookmark) {
        unsavedChanges = true;
        return bookmarks.remove(bookmark);
    }

    public Set<Bookmark> getBookmarks() {
        return new HashSet<>(bookmarks);
    }

    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    public void save(Path path) throws IOException {
        unsavedChanges = false;
        persistence.writeBookmarks(path, bookmarks);
    }

    public void load(Path filePath) throws IOException {
        unsavedChanges = false;
        bookmarks.clear();
        bookmarks.addAll(persistence.readBookmarks(filePath));
    }
}
