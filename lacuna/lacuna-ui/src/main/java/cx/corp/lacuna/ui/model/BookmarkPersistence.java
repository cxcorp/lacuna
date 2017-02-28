package cx.corp.lacuna.ui.model;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BookmarkPersistence {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;

    public BookmarkPersistence(Gson gson, Path savePath) {
        this.gson = gson;
    }

    public Set<Bookmark> readBookmarks(Path savePath) throws IOException {
        String json = new String(Files.readAllBytes(savePath), CHARSET);
        Bookmark[] bookmarks = gson.fromJson(json, Bookmark[].class);
        return new HashSet<Bookmark>(Arrays.<Bookmark>asList(bookmarks));
    }

    public void writeBookmarks(Path savePath, Set<Bookmark> bookmarks) throws IOException {
        String json = gson.toJson(bookmarks);
        byte[] bytes = json.getBytes(CHARSET);
        Files.write(savePath, bytes);
    }
}