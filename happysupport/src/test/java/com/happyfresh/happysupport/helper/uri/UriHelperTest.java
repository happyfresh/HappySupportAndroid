package com.happyfresh.happysupport.helper.uri;

import android.net.Uri;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UriHelperTest {

    private String stockLocationSlug;

    private String productSlug;

    private String categorySlug;

    /**
     * https://happyfresh.com/lotte-mart
     */
    @Test
    public void bind_test() {
        Uri uri = mock(Uri.class);
        List<String> pathSegmentsActual = new ArrayList<>();
        pathSegmentsActual.add("lotte-mart");

        doReturn(pathSegmentsActual).when(uri).getPathSegments();

        UriHelper.parse(this, uri);

        assertEquals("lotte-mart", stockLocationSlug);
        assertNull(productSlug);
        assertNull(categorySlug);
    }

    /**
     * https://happyfresh.com/lotte-mart/product/apple
     */
    @Test
    public void bind_product_test() {
        Uri uri = mock(Uri.class);
        List<String> pathSegmentsActual = new ArrayList<>();
        pathSegmentsActual.add("lotte-mart");
        pathSegmentsActual.add("product");
        pathSegmentsActual.add("apple");

        doReturn(pathSegmentsActual).when(uri).getPathSegments();

        UriHelper.parse(this, uri);

        assertEquals("lotte-mart", stockLocationSlug);
        assertEquals("apple", productSlug);
        assertNull(categorySlug);
    }

    /**
     * https://happyfresh.com/lotte-mart/checkout
     */
    @Test
    public void bind_checkout_test() {
        Uri uri = mock(Uri.class);
        List<String> pathSegmentsActual = new ArrayList<>();
        pathSegmentsActual.add("lotte-mart");
        pathSegmentsActual.add("checkout");

        doReturn(pathSegmentsActual).when(uri).getPathSegments();

        UriHelper.parse(this, uri);

        assertEquals("lotte-mart", stockLocationSlug);
        assertNull(productSlug);
        assertEquals("checkout", categorySlug);
    }

    /**
     * https://happyfresh.com/lotte-mart/fresh-produce
     */
    @Test
    public void bind_category_test() {
        Uri uri = mock(Uri.class);
        List<String> pathSegmentsActual = new ArrayList<>();
        pathSegmentsActual.add("lotte-mart");
        pathSegmentsActual.add("fresh-produce");

        doReturn(pathSegmentsActual).when(uri).getPathSegments();

        UriHelper.parse(this, uri);

        assertEquals("lotte-mart", stockLocationSlug);
        assertNull(productSlug);
        assertEquals("fresh-produce", categorySlug);
    }

    @UriParse("{stock_location_slug}")
    public void onBind(@UriPath("stock_location_slug") String stockLocationSlug) {
        this.stockLocationSlug = stockLocationSlug;
    }

    @UriParse("{stock_location_slug}/{category_slug}")
    public void onBindCategory(
            @UriPath("stock_location_slug") String stockLocationSlug,
            @UriPath("category_slug") String categorySlug
    ) {
        this.stockLocationSlug = stockLocationSlug;
        this.categorySlug = categorySlug;
    }

    @UriParse("{stock_location_slug}/product/{product_slug}")
    public void onBindProduct(
            @UriPath("stock_location_slug") String stockLocationSlug,
            @UriPath("product_slug") String productSlug
    ) {
        this.stockLocationSlug = stockLocationSlug;
        this.productSlug = productSlug;
    }
}
