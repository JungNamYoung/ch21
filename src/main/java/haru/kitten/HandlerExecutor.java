/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) [2018년] [SamuelSky]
 */
package haru.kitten;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import haru.define.Define;
import haru.logger.LoggerManager;
import haru.model.Model;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;

public class HandlerExecutor {

  static Logger logger = LoggerManager.getLogger(HandlerExecutor.class.getSimpleName());

  public static void execute(HandlerMapping handlerMapping, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {

    Model model = new Model();

    miniHttpServletResponse.setStatus(HttpServletResponse.SC_OK);

    Object result = HandlerExecutor.invokeHandler(handlerMapping, miniHttpServletRequest, miniHttpServletResponse, model);

    if (result instanceof String) {
      HandlerExecutor.renderView((String) result, miniHttpServletRequest, miniHttpServletResponse, model);
    } else if (result instanceof List<?>) {
      HandlerExecutor.renderList((List<?>) result, miniHttpServletResponse);
    } else if (result == null) {
      HandlerExecutor.renderJson(model, miniHttpServletResponse);
    } else {
      throw new RuntimeException(Define.NOT_APPLICABLE);
    }

    try {
      miniHttpServletResponse.flushBuffer();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  static Object invokeHandler(HandlerMapping handlerMapping, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, Model model) {
    Object result = null;
    try {
      Object targetBean = (handlerMapping.getBeanDefinition().getProxyInstance() != null) ? handlerMapping.getBeanDefinition().getProxyInstance() : handlerMapping.getBeanDefinition().getTargetBean();

      Method method = handlerMapping.getMethod();

      Object[] args = createArguments(method, model, miniHttpServletRequest, miniHttpServletResponse);

      result = method.invoke(targetBean, args);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return result;
  }

  static private Object[] createArguments(Method method, Model model, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {
    Parameter[] parameters = method.getParameters();
    Object[] args = new Object[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      Class<?> paramType = parameters[i].getType();
      if (paramType.equals(Model.class)) {
        args[i] = model;
      } else if (paramType.equals(MiniHttpServletRequest.class)) {
        args[i] = miniHttpServletRequest;
      } else if (paramType.equals(MiniHttpServletResponse.class)) {
        args[i] = miniHttpServletResponse;
      } else if (paramType.equals(MiniHttpSession.class)) {
        args[i] = miniHttpServletRequest.getSession();
      } else {

        try {
          Object commandObject = paramType.getDeclaredConstructor().newInstance();
          Field[] fields = paramType.getDeclaredFields();
          for (Field field : fields) {
            field.setAccessible(true);
            String paramValue = miniHttpServletRequest.getParameter(field.getName());
            if (paramValue != null) {
              field.set(commandObject, paramValue);
            }
          }
          args[i] = commandObject;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    return args;
  }

  static void renderView(String viewName, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, Model model) {
    String jspPath = Define.WEB_INF + "jsp/" + viewName + Define.EXT_JSP;

    for (Map.Entry<String, Object> entry : model.getAttributes().entrySet()) {
      logger.info("model - " + entry.getKey() + " - " + entry.getValue());
    }

    RequestDispatcher requestDispatcher = miniHttpServletRequest.getRequestDispatcher(jspPath);
    MiniRequestDispatcher miniRequestDispatcher = (MiniRequestDispatcher) requestDispatcher;

    try {
      miniRequestDispatcher.compileAndExecute(miniHttpServletRequest, miniHttpServletResponse, model.getAttributes());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  static void renderList(List<?> results, MiniHttpServletResponse miniHttpServletResponse) {
    miniHttpServletResponse.setContentType(Define.TEXT_PLAIN_EX);
    for (Object vo : results) {
      miniHttpServletResponse.getWriter().write(vo.toString() + Define.ENTER_EX);
    }
  }

  static void renderJson(Model model, MiniHttpServletResponse miniHttpServletResponse) {
    Object jsonData = model.getAttribute(Define.JSON);

    if (jsonData != null) {
      miniHttpServletResponse.setContentType(Define.APP_JSON + Define.CHARSET_UTF_8);
      miniHttpServletResponse.getWriter().write(jsonData.toString());
    }
  }
}
