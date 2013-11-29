/**
 * Tools for Authenticating your Users with GoInstant.
 *
 * Please read GoInstant guide on <a
 * href="https://developer.goinstant.com/v1/security_and_auth/index.html">Security
 * and Auth</a>. See the main <a href="https://goinstant.com">GoInstant</a>
 * website for what GoInstant is.
 * <br>
 * A {@link User} belongs to zero or more {@link Group}s.  These are both
 * declared as Interfaces so that you can tack them on easily to your existing
 * code via adapters.  For concrete implementations, use instead
 * {@link PlainUser} and {@link PlainGroup}.
 * <br>
 * The {@link Signer} class is where the action happens.  You constrct it with
 * your GoInstant secret key, pass in a {@link User}, and BOOM, out pops a
 * token, ready for passing into the GoInstant client!
 */
package com.goinstant.auth;
