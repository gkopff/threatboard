/*
 * Copyright 2014 Gregory Kopff
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package controllers;

import com.google.common.base.Preconditions;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * A Play authenticator which requires clients to log in. <p/>
 *
 * Our implementation uses {@code username} in exactly the same manner as the default authenticator,
 * so we only need to override the failed authorisation result. <p/>
 *
 * The Play framework invokes this class in a thread-safe manner - clients do not call it directly.
 */
@NotThreadSafe
public class ThreatboardAuthenticator extends Security.Authenticator
{
  /**
   * Redirects the unauthorised user to the login page.
   * @param ctx The context.
   * @return A redirect to the login page.
   */
  @Override
  public Result onUnauthorized(Http.Context ctx)
  {
    Preconditions.checkNotNull(ctx, "ctx cannot be null");

    return redirect(routes.AuthenticationController.loginForm());
  }
}
