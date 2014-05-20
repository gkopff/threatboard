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

import com.fatboyindustrial.crowdcontrol.Either;
import com.fatboyindustrial.crowdcontrol.Interactors;
import com.fatboyindustrial.crowdcontrol.model.AuthenticationError;
import com.fatboyindustrial.crowdcontrol.model.AuthenticationResponse;
import models.AuthModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.authenticate;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * The authentication controller is responsible for rendering the log in page and coordinating with
 * the Crowd authentication server.
 */
@NotThreadSafe
public class AuthenticationController extends Controller
{
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

  /**
   * Renders the login page.
   * @return The login page.
   */
  public static Result loginForm()
  {
    final Form<AuthModel> form = Form.form(AuthModel.class);
    return ok(authenticate.render(form));
  }

  /**
   * Processes the authentication request.
   * @return The index page if successful, otherwise the authentication page with errors.
   */
  public static Result doLogin()
  {
    final Form<AuthModel> bound = Form.form(AuthModel.class).bindFromRequest();
    if (bound.hasErrors())
    {
      flash("error", bound.errors().values().iterator().next().iterator().next().message());
      return badRequest(authenticate.render(bound));
    }

    if (! bound.value().isDefined())
    {
      return internalServerError();
    }

    final AuthModel model = bound.value().get();

    // Once the form is validated, all fields are non-null.

    try
    {
      final String baseUrl = Play.application().configuration().getString("application.crowd.baseUrl");
      final String appName = Play.application().configuration().getString("application.crowd.appName");
      final String appPass = Play.application().configuration().getString("application.crowd.appPass");

      final Either<AuthenticationResponse, AuthenticationError> result =
          Interactors.authentication(baseUrl, appName, appPass).execute(model.username, model.password);

      if (result.isError())
      {
        LOG.info("doLogin(): authentication failed: {} {}", result.getError().getReason(), result.getError().getMessage());

        flash("error", "Authentication failure");
        bound.data().put("password", "");

        return badRequest(authenticate.render(bound));
      }

      session().put("username", model.username);

      return redirect(routes.Application.index());
    }
    catch (Exception e)                                    // most likely we failed to communicate with Crowd
    {
      LOG.warn("doLogin(): unhandled exception.", e);
      return internalServerError();
    }
  }

  /**
   * Processes the logout request.
   * @return The index page.
   */
  public static Result doLogout()
  {
    session().clear();
    return redirect(routes.Application.index());
  }
}
