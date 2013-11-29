/**
 * Tools for Authenticating your Users with GoInstant.
 *
 * Please read GoInstant guide on <a
 * href="https://developer.goinstant.com/v1/security_and_auth/index.html">Security
 * and Auth</a>. See the main <a href="https://goinstant.com">GoInstant</a>
 * website for what GoInstant is.
 * <br>
 * A {@link com.goinstant.auth.User} belongs to zero or more {@link
 * com.goinstant.auth.Group}s.  These are both declared as Interfaces so that
 * you can tack them on easily to your existing code via adapters.  For
 * concrete implementations, use instead {@link com.goinstant.auth.PlainUser}
 * and {@link com.goinstant.auth.PlainGroup}.
 * <br>
 * The {@link com.goinstant.auth.Signer} class is where the action happens.
 * You constrct it with your GoInstant secret key, pass in a {@link
 * com.goinstant.auth.User}, and BOOM, out pops a token, ready for passing into
 * the GoInstant client!
 */
package com.goinstant.auth;
