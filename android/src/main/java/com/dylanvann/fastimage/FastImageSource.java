package com.dylanvann.fastimage;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import javax.annotation.Nullable;

public class FastImageSource {
    private static final String DATA_SCHEME = "data";
    private static final String LOCAL_RESOURCE_SCHEME = "res";
    private static final String ANDROID_RESOURCE_SCHEME = "android.resource";
    private static final String ANDROID_CONTENT_SCHEME = "content";
    private static final String LOCAL_FILE_SCHEME = "file";

    private final Headers mHeaders;
    private final Uri mUri;
    private final String mSource;
    private final double mWidth;
    private final double mHeight;

    public static boolean isBase64Uri(Uri uri) {
        return DATA_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalResourceUri(Uri uri) {
        return LOCAL_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isResourceUri(Uri uri) {
        return ANDROID_RESOURCE_SCHEME.equals(uri.getScheme());
    }

    public static boolean isContentUri(Uri uri) {
        return ANDROID_CONTENT_SCHEME.equals(uri.getScheme());
    }

    public static boolean isLocalFileUri(Uri uri) {
        return LOCAL_FILE_SCHEME.equals(uri.getScheme());
    }

    public FastImageSource(Context context, String source) {
        this(context, source, null, 0.0d, 0.0d);
    }

    public FastImageSource(Context context, String source, @Nullable Headers headers) {
        this(context, source, headers, 0.0d, 0.0d);
    }

    public FastImageSource(Context context, String source, @Nullable Headers headers, double width, double height) {
        this.mSource = source;
        this.mWidth = width;
        this.mHeight = height;
        this.mHeaders = headers == null ? Headers.DEFAULT : headers;

        Uri uri = Uri.parse(source);
        if (isLocalResourceUri(uri)) {
            // Convert res:/ scheme to android.resource:// so Glide can understand the uri.
            uri = Uri.parse(uri.toString().replace("res:/", ANDROID_RESOURCE_SCHEME + "://" + context.getPackageName() + "/"));
        }

        this.mUri = uri;

        if (isResource() && TextUtils.isEmpty(mUri.toString())) {
            throw new Resources.NotFoundException("Local Resource Not Found. Resource: '" + mSource + "'.");
        }
    }

    public boolean isBase64Resource() {
        return mUri != null && isBase64Uri(mUri);
    }

    public boolean isResource() {
        return mUri != null && isResourceUri(mUri);
    }

    public boolean isLocalFile() {
        return mUri != null && isLocalFileUri(mUri);
    }

    public boolean isContentUri() {
        return mUri != null && isContentUri(mUri);
    }

    public Object getSourceForLoad() {
        if (isContentUri()) {
            return mSource;
        }
        if (isBase64Resource()) {
            return mSource;
        }
        if (isResource()) {
            return mUri;
        }
        if (isLocalFile()) {
            return mUri.toString();
        }
        return getGlideUrl();
    }

    public Uri getUri() {
        return mUri;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public GlideUrl getGlideUrl() {
        return new GlideUrl(getUri().toString(), getHeaders());
    }

    public String getSource() {
        return mSource;
    }

    public double getWidth() {
        return mWidth;
    }

    public double getHeight() {
        return mHeight;
    }
}
