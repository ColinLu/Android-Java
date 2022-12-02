package com.colin.library.android.widgets.span;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * LinkSpan take a piece of text and a regular expression and turns all of the
 * regex matches in the text into clickable links.  This is particularly
 * useful for matching things like email addresses, web urls, etc. and making
 * them actionable.
 * <p>
 * Alone with the pattern that is to be matched, a url scheme prefix is also
 * required.  Any pattern match that does not begin with the supplied scheme
 * will have the scheme prepended to the matched text when the clickable url
 * is created.  For instance, if you are matching web urls you would supply
 * the scheme <code>http://</code>.  If the pattern matches example.com, which
 * does not have a url scheme prefix, the supplied scheme will be prepended to
 * create <code>http://example.com</code> when the clickable url link is
 * created.
 */

public class LinkSpan {

    public static final Pattern WECHAT_PHONE = Pattern.compile("\\+?(\\d{2,8}([- ]?\\d{3,8}){2,6}|\\d{5,20})");

    // 其他数字的情况
    public static final Pattern NOT_PHONE = Pattern.compile("^\\d+(\\.\\d+)+(-\\d+)*$");
    private static final int MAX_NUMBER = 21;
    private static final String URL_END_APPEND_NEXT_CHARS = "[$]";
    private static final String[] SCHEME_WEB_URL = new String[]{"http://", "https://", "rtsp://"};
    private static final String[] SCHEME_EMAIL = new String[]{"mailto:"};
    private static final String[] SCHEME_PHONE_NUMBER = new String[]{"tel:"};

    /**
     * Bit field indicating that web URLs should be matched in methods that
     * take an options mask
     */
    public static final int WEB_URLS = 0x01;

    /**
     * Bit field indicating that email addresses should be matched in methods
     * that take an options mask
     */
    public static final int EMAIL_ADDRESSES = 0x02;

    /**
     * Bit field indicating that phone numbers should be matched in methods that
     * take an options mask
     */
    public static final int PHONE_NUMBERS = 0x04;

    /**
     * Bit field indicating that street addresses should be matched in methods that
     * take an options mask
     */
    public static final int MAP_ADDRESSES = 0x08;

    /**
     * Bit mask indicating that all available patterns should be matched in
     * methods that take an options mask
     */
    public static final int ALL = WEB_URLS | EMAIL_ADDRESSES | PHONE_NUMBERS | MAP_ADDRESSES;

    /**
     * Don't treat anything with fewer than this many digits as a
     * phone number.
     */
    private static final int PHONE_NUMBER_MINIMUM_DIGITS = 7;

    public static final WebUrlMatcher WEB_URL_MATCHER = () -> WebUrlPattern.WEB_URL;

    private static WebUrlMatcher sWebUrlMatcher = () -> Patterns.WEB_URL;

    public LinkSpan() {
    }


    /**
     * Filters out web URL matches that occur after an at-sign (@).  This is
     * to prevent turning the domain name in an email address into a web link.
     */
    public static final MatchFilter MATCH_FILTER_WEB_URL = (s, start, end) -> {
        try {
            for (int i = start; i < end; ++i) if (s.charAt(i) > 256) return false;
            try {
                char next = s.charAt(end);
                if (next < 256 && !((0 <= URL_END_APPEND_NEXT_CHARS.indexOf(next)) || Character.isWhitespace(next))) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (start == 0) return true;
            if (s.charAt(start - 1) == '@') return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    };

    /**
     * Filters out URL matches that don't have enough digits to be a
     * phone number.
     */
    public static final MatchFilter MATCH_FILTER_PHONE_NUMBER = (s, start, end) -> {
        int digitCount = 0;
        for (int i = start; i < end; i++) {
            if (Character.isDigit(s.charAt(i))) {
                digitCount++;
                if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) return true;
            }
        }
        return false;
    };

    /**
     * Transforms matched phone number text into something suitable
     * to be used in a tel: URL.  It does this by removing everything
     * but the digits and plus signs.  For instance:
     * &apos;+1 (919) 555-1212&apos;
     * becomes &apos;+19195551212&apos;
     */
    public static final TransformFilter TRANSFORM_FILTER_PHONE_NUMBER = (match, url) -> Patterns.digitsAndPlusOnly(match);

    public static void useWebUrlMatcher() {
        sWebUrlMatcher = WEB_URL_MATCHER;
    }

    public static void setWebUrlMatcher(WebUrlMatcher webUrlMatcher) {
        sWebUrlMatcher = webUrlMatcher;
    }

    /**
     * Scans the view of the provided TextView and turns all occurrences of
     * the link types indicated in the mask into clickable links.  If matches
     * are found the movement method for the TextView is set to
     * LinkMovementMethod.
     */
    public static boolean addLinks(@NonNull TextView view, int mask, @Nullable ColorStateList link, @Nullable ColorStateList bg, @Nullable OnClickSpanListener listener) {
        if (mask == 0) return false;
        final Spannable span = getSpannable(view);
        if (span == null) return false;
        if (addLinks(span, mask, link, bg, listener)) {
            addLinkMovementMethod(view);
            return true;
        }
        return false;
    }

    private static void addLinkMovementMethod(@NonNull TextView view) {
        final MovementMethod method = view.getMovementMethod();
        if (!(method instanceof LinkMovementMethod)) {
            if (view.getLinksClickable()) view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private static Spannable getSpannable(@NonNull TextView view) {
        final CharSequence text = view.getText();
        if (TextUtils.isEmpty(text)) return null;
        if (text instanceof Spannable) return (Spannable) text;
        else {
            final SpannableString span = SpannableString.valueOf(text);
            view.setText(span);
            return span;
        }
    }

    /**
     * Applies a regex to the view of a TextView turning the matches into
     * links.  If links are found then UrlSpans are applied to the link
     * view match areas, and the movement method for the view is changed
     * to LinkMovementMethod.
     *
     * @param view    TextView whose view is to be marked-up with links
     * @param pattern Regex pattern to be used for finding links
     * @param scheme  Url scheme string (eg <code>http://</code> to be
     *                prepended to the url of links that do not have
     *                a scheme specified in the link text
     */
    public static void addLinks(@NonNull TextView view, @NonNull Pattern pattern, @Nullable String scheme) {
        addLinks(view, pattern, scheme, null, null);
    }

    /**
     * Applies a regex to the text of a TextView turning the matches into
     * links.  If links are found then UrlSpans are applied to the link
     * text match areas, and the movement method for the text is changed
     * to LinkMovementMethod.
     *
     * @param view    TextView whose text is to be marked-up with links
     * @param pattern Regex pattern to be used for finding links
     * @param scheme  Url scheme string (eg <code>http://</code> to be
     *                prepended to the url of links that do not have
     *                a scheme specified in the link text
     * @param match   The filter that is used to allow the client code
     *                additional control over which pattern matches are
     *                to be converted into links.
     */
    public static void addLinks(@NonNull TextView view, @NonNull Pattern pattern, @Nullable String scheme,
                                @Nullable MatchFilter match, @Nullable TransformFilter transform) {
        final Spannable span = getSpannable(view);
        if (span == null) return;
        if (addLinks(span, pattern, scheme, match, transform)) addLinkMovementMethod(view);
    }

    /**
     * Applies a regex to a Spannable turning the matches into
     * links.
     *
     * @param span    Spannable whose text is to be marked-up with
     *                links
     * @param pattern Regex pattern to be used for finding links
     * @param scheme  Url scheme string (eg <code>http://</code> to be
     *                prepended to the url of links that do not have
     *                a scheme specified in the link text
     */
    public static boolean addLinks(@NonNull Spannable span, @NonNull Pattern pattern, @Nullable String scheme) {
        return addLinks(span, pattern, scheme, null, null);
    }

    /**
     * Applies a regex to a Spannable turning the matches into
     * links.
     *
     * @param span    Spannable whose text is to be marked-up with
     *                links
     * @param pattern Regex pattern to be used for finding links
     * @param scheme  Url scheme string (eg <code>http://</code> to be
     *                prepended to the url of links that do not have
     *                a scheme specified in the link text
     * @param match   The filter that is used to allow the client code
     *                additional control over which pattern matches are
     *                to be converted into links.
     */
    public static boolean addLinks(@NonNull Spannable span, @NonNull Pattern pattern, @Nullable String scheme,
                                   @Nullable MatchFilter match, @Nullable TransformFilter transform) {
        boolean hasMatches = false;
        final String prefix = scheme == null ? "" : scheme.toLowerCase(Locale.ROOT);
        final Matcher matcher = pattern.matcher(span);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            boolean allowed = true;
            if (match != null) allowed = match.acceptMatch(span, start, end);
            if (allowed) {
                String url = makeUrl(matcher.group(0), new String[]{prefix}, matcher, transform);
                applyLink(url, start, end, span, null, null, null);
                hasMatches = true;
            }
        }

        return hasMatches;
    }

    /**
     * Scans the text of the provided Spannable and turns all occurrences
     * of the link types indicated in the mask into clickable links.
     * If the mask is nonzero, it also removes any existing URLSpans
     * attached to the Spannable, to avoid problems if you call it
     * repeatedly on the same text.
     */
    public static boolean addLinks(@Nullable Spannable span, int mask, @Nullable ColorStateList link,
                                   @Nullable ColorStateList bg, @Nullable OnClickSpanListener listener) {
        if (span == null || mask == 0) return false;

        final URLSpan[] old = span.getSpans(0, span.length(), URLSpan.class);

        for (int i = old.length - 1; i >= 0; i--) span.removeSpan(old[i]);
        final List<LinkSpec> specs = new ArrayList<>();
        if ((mask & WEB_URLS) != 0)
            gatherLinks(specs, span, sWebUrlMatcher.getPattern(), SCHEME_WEB_URL, MATCH_FILTER_WEB_URL);

        if ((mask & EMAIL_ADDRESSES) != 0)
            gatherLinks(specs, span, Patterns.EMAIL_ADDRESS, SCHEME_EMAIL, null);


        if ((mask & PHONE_NUMBERS) != 0)
            gatherPhoneLinks(specs, span, WECHAT_PHONE, new Pattern[]{NOT_PHONE},
                    SCHEME_PHONE_NUMBER, MATCH_FILTER_PHONE_NUMBER, TRANSFORM_FILTER_PHONE_NUMBER);


        if ((mask & MAP_ADDRESSES) != 0) gatherMapLinks(specs, span);

        pruneOverlaps(specs);

        if (specs.size() == 0) return false;

        for (LinkSpec spec : specs)
            applyLink(spec.url, spec.start, spec.end, span, link, bg, listener);

        return true;
    }

    private static void applyLink(@Nullable String url, int start, int end, Spannable span, final ColorStateList link, final ColorStateList bg, @Nullable OnClickSpanListener listener) {
        if (TextUtils.isEmpty(url)) return;
        span.setSpan(new StyleableURLSpan(url, listener) {

            @Override
            public void updateDrawState(TextPaint paint) {
                if (link != null) {
                    int normalLinkColor = link.getColorForState(new int[]{android.R.attr.state_enabled, -android.R.attr.state_pressed}, Color.TRANSPARENT);
                    int pressedLinkColor = link.getColorForState(new int[]{android.R.attr.state_pressed}, normalLinkColor);
                    paint.linkColor = mPressed ? pressedLinkColor : normalLinkColor;
                }
                if (bg != null) {
                    int normalBgColor = bg.getColorForState(new int[]{android.R.attr.state_enabled, -android.R.attr.state_pressed}, Color.TRANSPARENT);
                    int pressedBgColor = bg.getColorForState(new int[]{android.R.attr.state_pressed}, normalBgColor);
                    paint.bgColor = mPressed ? pressedBgColor : normalBgColor;
                }
                super.updateDrawState(paint);
                paint.setUnderlineText(false);
            }

        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static abstract class StyleableURLSpan extends URLSpan implements ITouchableSpan {
        protected final String mUrl;
        protected final OnClickSpanListener mOnSpanClickListener;
        protected boolean mPressed = false;

        public StyleableURLSpan(@NonNull String url, @Nullable OnClickSpanListener listener) {
            super(url);
            this.mUrl = url;
            this.mOnSpanClickListener = listener;
        }

        @Override
        public void setPressed(boolean pressed) {
            this.mPressed = pressed;
        }

        @Override
        public void onClick(@NonNull View view) {
            if (mOnSpanClickListener != null && mOnSpanClickListener.onClick(mUrl)) return;
            super.onClick(view);
        }

    }

    private static void gatherLinks(@NonNull List<LinkSpec> specs, @NonNull Spannable span, @NonNull Pattern pattern, String[] schemes, MatchFilter matchFilter) {
        final Matcher matcher = pattern.matcher(span);
        while (matcher.find()) {
            final int start = matcher.start();
            final int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(span, start, end)) {
                final LinkSpec spec = new LinkSpec();
                spec.url = makeUrl(matcher.group(0), schemes, matcher, null);
                spec.start = start;
                spec.end = end;
                specs.add(spec);
            }
        }
    }


    private static void gatherPhoneLinks(List<LinkSpec> links,
                                         Spannable s, Pattern pattern, Pattern[] excepts, String[] schemes,
                                         MatchFilter matchFilter, TransformFilter transformFilter) {
        final Matcher m = pattern.matcher(s);

        while (m.find()) {
            if (isInExcepts(m.group(), excepts)) continue;
            final int start = m.start();
            final int end = m.end();

            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                final LinkSpec spec = new LinkSpec();
                spec.url = makeUrl(m.group(0), schemes, m, transformFilter);
                spec.start = start;
                spec.end = end;
                links.add(spec);
            }
        }
    }

    private static void gatherMapLinks(@NonNull List<LinkSpec> links, @NonNull Spannable s) {
        String string = s.toString();
        String address;
        int base = 0;
        try {
            while ((address = WebView.findAddress(string)) != null) {
                int start = string.indexOf(address);
                if (start < 0) break;
                final LinkSpec spec = new LinkSpec();
                final int length = address.length();
                final int end = start + length;
                spec.start = base + start;
                spec.end = base + end;
                string = string.substring(end);
                base += end;

                String encodedAddress;

                try {
                    encodedAddress = URLEncoder.encode(address, Encode.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    continue;
                }
                spec.url = "geo:0,0?q=" + encodedAddress;
                links.add(spec);
            }
        } catch (UnsupportedOperationException e) {
            // findAddress may fail with an unsupported exception on platforms without a WebView.
            // In this case, we will not append anything to the links variable: it would have died
            // in WebView.findAddress.
        }
    }


    private static String makeUrl(@Nullable String url, String[] prefixes, Matcher matcher, TransformFilter filter) {
        if (filter != null) url = filter.transformUrl(matcher, url);
        if (TextUtils.isEmpty(url)) return null;
        boolean hasPrefix = false;
        for (String pre : prefixes) {
            if (url.regionMatches(true, 0, pre, 0, pre.length())) {
                hasPrefix = true;
                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, pre, 0, pre.length())) {
                    url = pre + url.substring(pre.length());
                }
                break;
            }
        }
        if (!hasPrefix) url = prefixes[0] + url;
        return url;
    }

    private static boolean isInExcepts(@NonNull CharSequence data, Pattern[] excepts) {
        for (Pattern except : excepts) {
            final Matcher matcher = except.matcher(data);
            if (matcher.find()) return true;
        }
        return isTooLarge(data);
    }


    private static boolean isTooLarge(@NonNull CharSequence data) {
        if (data.length() <= MAX_NUMBER) return false;

        final int count = data.length();
        int digitCount = 0;
        for (int i = 0; i < count; i++) {
            if (Character.isDigit(data.charAt(i))) {
                digitCount++;
                if (digitCount > MAX_NUMBER) return true;
            }
        }

        return false;
    }


    private static void pruneOverlaps(@NonNull List<LinkSpec> links) {
        Comparator<LinkSpec> c = (a, b) -> {
            if (a.start < b.start) return -1;
            if (a.start > b.start) return 1;
            return Integer.compare(b.end, a.end);
        };

        Collections.sort(links, c);
        int len = links.size(), i = 0;
        LinkSpec a, b;
        while (i < len - 1) {
            a = links.get(i);
            b = links.get(i + 1);
            int remove = -1;
            if ((a.start <= b.start) && (a.end > b.start)) {
                if (b.end <= a.end) remove = i + 1;
                else if ((a.end - a.start) > (b.end - b.start)) remove = i + 1;
                else if ((a.end - a.start) < (b.end - b.start)) remove = i;
                if (remove != -1) {
                    links.remove(remove);
                    len--;
                    continue;
                }
            }
            i++;
        }
    }

    private static class LinkSpec {
        String url;
        int start;
        int end;
    }

    private static class WebUrlPattern {
        private static final String PROTOCOL = "(?i:http|https|rtsp)://";
        // all domain names
        private static final String[] EXT = {
                "top", "com", "net", "org", "edu", "gov", "int", "mil", "tel", "biz", "cc", "tv", "info", "zw",
                "name", "hk", "mobi", "asia", "cd", "travel", "pro", "museum", "coop", "aero", "ad", "ae", "af",
                "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az", "ba", "bb", "bd",
                "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz",
                "ca", "cc", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cq", "cr", "cu", "cv", "cx",
                "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh", "es", "et", "ev", "fi",
                "fj", "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gh", "gi", "gl", "gm", "gn", "gp",
                "gr", "gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "in", "io",
                "iq", "ir", "is", "it", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw",
                "ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md",
                "mg", "mh", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mv", "mw", "mx", "my", "mz",
                "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nt", "nu", "nz", "om", "qa", "pa",
                "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "pt", "pw", "py", "re", "ro", "ru", "rw",
                "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", "sm", "sn", "so", "sr", "st",
                "su", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk", "tm", "tn", "to", "tp", "tr", "tt",
                "tv", "tw", "tz", "ua", "ug", "uk", "us", "uy", "va", "vc", "ve", "vg", "vn", "vu", "wf", "ws",
                "ye", "yu", "za", "zm", "zr"
        };

        private static final String IP_ADDRESS =
                "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                        + "|[1-9][0-9]|[0-9]))";
        /**
         * Valid UCS characters defined in RFC 3987. Excludes space characters.
         */
        private static final String UCS_CHAR = "[" +
                "\u00A0-\uD7FF" +
                "\uF900-\uFDCF" +
                "\uFDF0-\uFFEF" +
                "\uD800\uDC00-\uD83F\uDFFD" +
                "\uD840\uDC00-\uD87F\uDFFD" +
                "\uD880\uDC00-\uD8BF\uDFFD" +
                "\uD8C0\uDC00-\uD8FF\uDFFD" +
                "\uD900\uDC00-\uD93F\uDFFD" +
                "\uD940\uDC00-\uD97F\uDFFD" +
                "\uD980\uDC00-\uD9BF\uDFFD" +
                "\uD9C0\uDC00-\uD9FF\uDFFD" +
                "\uDA00\uDC00-\uDA3F\uDFFD" +
                "\uDA40\uDC00-\uDA7F\uDFFD" +
                "\uDA80\uDC00-\uDABF\uDFFD" +
                "\uDAC0\uDC00-\uDAFF\uDFFD" +
                "\uDB00\uDC00-\uDB3F\uDFFD" +
                "\uDB44\uDC00-\uDB7F\uDFFD" +
                "&&[^\u00A0[\u2000-\u200A]\u2028\u2029\u202F\u3000]]";

        /**
         * Valid characters for IRI label defined in RFC 3987.
         */
        private static final String LABEL_CHAR = "a-zA-Z0-9" + UCS_CHAR;
        private static final String PORT_NUMBER = "\\:\\d{1,5}";
        private static final String PATH_AND_QUERY = "[/\\?](?:(?:[" + LABEL_CHAR
                + ";/\\?:@&=#~"  // plus optional query params
                + "\\-\\.\\+!\\*'\\(\\),_\\$])|(?:%[a-fA-F0-9]{2}))*";
        private static Pattern WEB_URL;


        static {
            final StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < EXT.length; i++) {
                if (i != 0) sb.append("|");
                sb.append(EXT[i]);
            }
            sb.append(")");
            final String host = "((?:(www\\.|[a-zA-Z\\.\\-]+\\.)?[a-zA-Z0-9\\-]+)" + "\\." + sb + ")";
            WEB_URL = Pattern.compile("("
                    + "(" + PROTOCOL + ")?"
                    + "(" + IP_ADDRESS + "|" + host + ")"
                    + "(" + PORT_NUMBER + ")?"
                    + "(" + PATH_AND_QUERY + ")?"
                    + ")");
        }
    }

    public interface WebUrlMatcher {
        @NonNull
        Pattern getPattern();
    }

    /**
     * MatchFilter enables client code to have more control over
     * what is allowed to match and become a link, and what is not.
     * <p>
     * For example:  when matching web urls you would like things like
     * http://www.example.com to match, as well as just example.com itelf.
     * However, you would not want to match against the domain in
     * support@example.com.  So, when matching against a web url pattern you
     * might also include a MatchFilter that disallows the match if it is
     * immediately preceded by an at-sign (@).
     */
    public interface MatchFilter {
        /**
         * Examines the character span matched by the pattern and determines
         * if the match should be turned into an actionable link.
         *
         * @param s     The body of text against which the pattern
         *              was matched
         * @param start The index of the first character in s that was
         *              matched by the pattern - inclusive
         * @param end   The index of the last character in s that was
         *              matched - exclusive
         * @return Whether this match should be turned into a link
         */
        boolean acceptMatch(CharSequence s, int start, int end);
    }

    /**
     * TransformFilter enables client code to have more control over
     * how matched patterns are represented as URLs.
     * <p>
     * For example:  when converting a phone number such as (919)  555-1212
     * into a tel: URL the parentheses, white space, and hyphen need to be
     * removed to produce tel:9195551212.
     */
    public interface TransformFilter {
        /**
         * Examines the matched text and either passes it through or uses the
         * data in the Matcher state to produce a replacement.
         *
         * @param match The regex matcher state that found this URL text
         * @param url   The text that was matched
         * @return The transformed form of the URL
         */
        @Nullable
        String transformUrl(@NonNull Matcher match, @Nullable String url);
    }
}
