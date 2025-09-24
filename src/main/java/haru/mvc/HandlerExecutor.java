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
package haru.mvc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import haru.annotation.mvc.RequestParam;
import haru.constants.Define;
import haru.http.MiniHttpServletRequest;
import haru.http.MiniHttpServletResponse;
import haru.http.MiniHttpSession;
import haru.logging.LoggerManager;
import haru.mvc.model.Model;
import haru.mvc.model.ModelMap;
import haru.servlet.view.MiniRequestDispatcher;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;

public class HandlerExecutor {

  private static final Logger logger = LoggerManager.getLogger(HandlerExecutor.class.getSimpleName());

  public static void execute(HandlerMapping handlerMapping, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse) {

    logger.info("실행");
    
    Model model = new ModelMap();

    miniHttpServletResponse.setStatus(HttpServletResponse.SC_OK);

    Object result = invokeHandler(handlerMapping, miniHttpServletRequest, miniHttpServletResponse, model);

    if (result instanceof String) {
      renderView((String) result, miniHttpServletRequest, miniHttpServletResponse, model);
    } else if (result instanceof List<?>) {
      renderList((List<?>) result, miniHttpServletResponse);
    } else if (result instanceof Map<?, ?>) {
      renderJsonEx((Map<?, ?>) result, miniHttpServletResponse);
    } else if (result == null) {
      renderJson(model, miniHttpServletResponse);
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
      Parameter parameter = parameters[i];
      Class<?> paramType = parameter.getType();

      if (parameter.isAnnotationPresent(RequestParam.class)) {
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        String paramName = requestParam.value();

        if (paramName == null || paramName.isEmpty()) {
          paramName = parameter.getName();
        }

        String paramValue = miniHttpServletRequest.getParameter(paramName);

        if (paramValue == null && requestParam.required()) {
          throw new IllegalArgumentException("Required request parameter '" + paramName + "' is missing");
        }

        args[i] = convertParameterValue(paramValue, paramType);
      } else if (model != null && paramType.isInstance(model)) {
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

  private static Object convertParameterValue(String paramValue, Class<?> targetType) {
    if (paramValue == null) {
      if (targetType.isPrimitive()) {
        if (targetType.equals(boolean.class)) {
          return false;
        } else if (targetType.equals(char.class)) {
          return '\0';
        } else if (targetType.equals(byte.class)) {
          return (byte) 0;
        } else if (targetType.equals(short.class)) {
          return (short) 0;
        } else if (targetType.equals(int.class)) {
          return 0;
        } else if (targetType.equals(long.class)) {
          return 0L;
        } else if (targetType.equals(float.class)) {
          return 0.0f;
        } else if (targetType.equals(double.class)) {
          return 0.0d;
        }
      }
      return null;
    }

    if (String.class.equals(targetType)) {
      return paramValue;
    } else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
      return Integer.parseInt(paramValue);
    } else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
      return Long.parseLong(paramValue);
    } else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
      return Double.parseDouble(paramValue);
    } else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
      return Float.parseFloat(paramValue);
    } else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
      return Boolean.parseBoolean(paramValue);
    } else if (Short.class.equals(targetType) || short.class.equals(targetType)) {
      return Short.parseShort(paramValue);
    } else if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
      return Byte.parseByte(paramValue);
    } else if (Character.class.equals(targetType) || char.class.equals(targetType)) {
      if (paramValue.length() != 1) {
        throw new IllegalArgumentException("Cannot convert parameter value '" + paramValue + "' to char");
      }
      return paramValue.charAt(0);
    } else if (Enum.class.isAssignableFrom(targetType)) {
      @SuppressWarnings({ "unchecked", "rawtypes" })
      Class<? extends Enum> enumType = (Class<? extends Enum>) targetType.asSubclass(Enum.class);
      return Enum.valueOf(enumType, paramValue);
    }
    return paramValue;
  }

  static void renderView(String viewName, MiniHttpServletRequest miniHttpServletRequest, MiniHttpServletResponse miniHttpServletResponse, Model model) {
    String jspPath = Define.WEB_INF_EX + "jsp/" + viewName + Define.EXT_JSP;

//    for (Map.Entry<String, Object> entry : model.getAttributes().entrySet()) {
//      logger.info("model - " + entry.getKey() + " - " + entry.getValue());
//    }

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

  static void renderJsonEx(Map<?, ?> results, MiniHttpServletResponse miniHttpServletResponse) {
    miniHttpServletResponse.setContentType(Define.APP_JSON + Define.CHARSET_UTF_8);
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      String jsonString = objectMapper.writeValueAsString(results);
      miniHttpServletResponse.getWriter().write(jsonString);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void renderJson(Model model, MiniHttpServletResponse miniHttpServletResponse) {
    Object jsonData = model.getAttribute(Define.JSON);

    if (jsonData != null) {

      ObjectMapper objectMapper = new ObjectMapper();

      try {
        String jsonString = objectMapper.writeValueAsString(jsonData);
        miniHttpServletResponse.setContentType(Define.APP_JSON + Define.CHARSET_UTF_8);
        miniHttpServletResponse.getWriter().write(jsonString);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
