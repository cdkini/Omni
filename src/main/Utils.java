package src.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/* Assorted utilities.
   @author P. N. Hilfinger */
public class Utils {

    /* SHA-1 HASH VALUES. */

    /* Returns the SHA-1 hash of the concatenation of VALS, which may be any
       mixture of byte arrays and Strings. */
    public static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /* Returns the SHA-1 hash of the concatenation of the strings in VALS. */
    public static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /* Deletes FILE if it exists and is not a directory.  Returns true if FILE
       was deleted, and false otherwise.  Refuses to delete FILE and throws
       IllegalArgumentException unless the directory designated by FILE also
       contains a directory named .omni. */
    public static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".omni")).isDirectory()) {
            throw new IllegalArgumentException("not .omni working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* Deletes the file named FILE if it exists and is not a directory. Returns
       true if FILE was deleted, and false otherwise. Refuses to delete FILE and
       throws IllegalArgumentException unless the directory designated by FILE
       also contains a directory named .omni. */
    public static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /* Return the entire contents of FILE as a byte array. FILE must be a normal
       file. Throws IllegalArgumentException in case of problems. */
    public static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* Write the entire contents of BYTES to FILE, creating or overwriting it as
       needed. Throws IllegalArgumentException in case of problems. */
    public static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* OTHER FILE UTILITIES */

    /* Return the concatenation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /* Return the concatenation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    public static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* DIRECTORIES */

    /* Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return new File(dir, name).isFile();
        }
    };

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    public static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    public static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    public static Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != null) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keySet().iterator();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}