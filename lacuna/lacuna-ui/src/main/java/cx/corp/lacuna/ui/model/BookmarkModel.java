package cx.corp.lacuna.ui.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class BookmarkModel {

    private final Set<Bookmark> bookmarks = new HashSet<>();
    private final BookmarkPersistence persistence;

    public BookmarkModel(BookmarkPersistence persistence) {
        this.persistence = persistence;
    }

    public boolean addBookmark(Bookmark bookmark) {
        return bookmarks.add(bookmark);
    }

    public Set<Bookmark> getBookmarks() {
        return new HashSet<>(bookmarks);
    }

    public void save(Path path) throws IOException {
        persistence.writeBookmarks(path, bookmarks);
    }

    public void reload(Path filePath) throws IOException {
        bookmarks.clear();
        bookmarks.addAll(persistence.readBookmarks(filePath));
    }
}
