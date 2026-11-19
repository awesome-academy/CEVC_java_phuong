package com.foodorder.foodapp.service.social;

/**
 * The user as the provider vouches for them, read from a verified token.
 *
 * @param emailVerified the provider confirmed the user owns this email
 */
public record SocialProfile(String email, boolean emailVerified, String name) {
}
