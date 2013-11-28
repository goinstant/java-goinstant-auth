/**
 * Tools for Authenticating your Users with GoInstant.
 *
 * Please read the GoInstant Security and Auth documentation at
 * {@link https://developers.goinstant.com/v1/security_and_auth/index.html}
 *
 * A {@link User} belongs to zero or more {@link Group}s.  These are both
 * declared as Interfaces so that you can tack them on easily to your existing
 * code via adapters.  For concrete implementations, use instead
 * {@link PlainUser} and {@link PlainGroup}.
 *
 * The {@link Signer} class is where the action happens.  You constrct it with
 * your GoInstant secret key, pass in a {@link User}, and BOOM, out pops a
 * token, ready for passing into the GoInstant client!
 */
package com.goinstant.auth;
