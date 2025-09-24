package haru.support;

public final class TypeConverter {
  private TypeConverter() {
  }

  public static boolean isSimpleType(Class<?> t) {
    return t.isPrimitive() || t.equals(String.class) || Number.class.isAssignableFrom(t) || t.equals(Boolean.class) || t.equals(Character.class) || Enum.class.isAssignableFrom(t);
  }

  public static Object convert(String raw, Class<?> targetType) {
    if (raw == null) {
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
      return raw;
    } else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
      return Integer.parseInt(raw);
    } else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
      return Long.parseLong(raw);
    } else if (Double.class.equals(targetType) || double.class.equals(targetType)) {
      return Double.parseDouble(raw);
    } else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
      return Float.parseFloat(raw);
    } else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
      return Boolean.parseBoolean(raw);
    } else if (Short.class.equals(targetType) || short.class.equals(targetType)) {
      return Short.parseShort(raw);
    } else if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
      return Byte.parseByte(raw);
    } else if (Character.class.equals(targetType) || char.class.equals(targetType)) {
      if (raw.length() != 1) {
        throw new IllegalArgumentException("Cannot convert parameter value '" + raw + "' to char");
      }
      return raw.charAt(0);
    } else if (Enum.class.isAssignableFrom(targetType)) {
      @SuppressWarnings({ "unchecked", "rawtypes" })
      Class<? extends Enum> enumType = (Class<? extends Enum>) targetType.asSubclass(Enum.class);
      return Enum.valueOf(enumType, raw);
    }
    return raw;
  }
}