package citrea.swarm4j.spec;

import java.util.Date;

/**
 * One token id from specifier.
 *
 * @see Spec
 *
 * Created with IntelliJ IDEA.
 * @author aleksisha
 *         Date: 27/10/13
 *         Time: 12:54
 */
public class SpecToken {

    public static final String RS_TOK = "[0-9A-Za-z_@]+";
    public static final String RS_TOK_EXT = "^(=)(?:\\+(=))?$".replaceAll("=", RS_TOK);
    public static final String NO_AUTHOR = "&_";

    // "versn+user~ext"
    private String str;
    private boolean parsed;

    // parsed parts
    private String bare;
    private String ext;

    public SpecToken(String tokenAsString) {
        this.str = tokenAsString;
        this.parsed = false;
    }

    /** Swarm employs 30bit integer Unix-like timestamps starting epoch at
     *  1 Jan 2010. Timestamps are encoded as 5-char base64 tokens; in case
     *  several events are generated by the same process at the same second
     *  then sequence number is added so a timestamp may be more than 5
     *  chars.
     */
    public static String date2ts(Date date) {
        long time = date.getTime();
        time -= Spec.EPOCH;
        return int2base((int) (time / 1000), 5);
    }

    private static final String PADDING_ZEROS = "0000000000000000";

    public static String int2base(int i, Integer padlen) {
        String ret = "";
        while (i != 0) {
            ret = Spec.BASE64.charAt(i & 63) + ret;
            i >>= 6;
        }
        if (padlen != null && ret.length() < padlen) {
            ret = PADDING_ZEROS.substring(0, padlen - ret.length()) + ret;
        }
        return ret;
    }

    private void ensureParsed() {
        if (parsed) { return; }

        this.bare = SpecToken.bare(str);
        this.ext = SpecToken.ext(str);
        if (this.ext.length() == 0) {
            this.ext = "@_";
        }
        this.parsed = true;
    }

    /**
     * @return bare part of spec token
     */
    public String getBare() {
        ensureParsed();
        return bare;
    }

    public void setBare(String bare) {
        ensureParsed();
        this.bare = bare;
        this.str = bare + (NO_AUTHOR.equals(ext) ? "" : "+" + ext);
    }

    /**
     * @return ext part of spec token
     */
    public String getExt() {
        ensureParsed();
        return ext;
    }

    public void setExt(String ext) {
        ensureParsed();
        if (ext != null && ext.length() > 0) {
            this.ext = ext;
            this.str = bare + "+" + ext;
        } else {
            this.ext = NO_AUTHOR;
            this.str = bare;
        }
    }

    /**
     * @param q quant for prepending id
     * @return token with quant as a string
     */
    public String toToken(SpecQuant q) {
        return String.valueOf(q.code) + str;
    }

    @Override
    public String toString() {
        return this.str;
    }

    @Override
    public int hashCode() {
        return this.str.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.str.equals(String.valueOf(o));
    }

    public static String bare(String tokAsString) {
        int pos = tokAsString.indexOf("+");
        return (pos > -1 ? tokAsString.substring(0, pos) : tokAsString);
    }

    public static String ext(String tokAsString) {
        int pos = tokAsString.indexOf("+");
        return (pos > -1 ? tokAsString.substring(pos + 1) : "");
    }

}
