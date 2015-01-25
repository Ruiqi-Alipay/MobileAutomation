package com.alipay.automation.utils;

/**
 * A path filter.
 * Two types of wildcards are supported:
 * <ul>
 * <li><code>*</code> - match any char from a path segment
 * <li><code>**</code> - match any path segment
 * </ul>
 *
 */
public interface PathFilter {

    boolean accept(Path path);

    boolean isExclusive();

}
