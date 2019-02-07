package fr.umlv.smalljs.stackinterp;

interface TagValues {
  static boolean isSmallInt(int value) {
    return (value & 0b1) == 0b1;
  }

  static int encodeSmallInt(int value) {
    return value << 0b1 | 0b1;
  }
  static int decodeSmallInt(int value) {
    return value >>> 0b1;
  }

  static int encodeObject(Object object, Dictionary dict) {
    return dict.index(object) << 1;
  }
  static Object decodeObject(int value, Dictionary dict) {
    return dict.getConst(value >>> 1);
  }

  static Object decodeAnyValue(int tagValue, Dictionary dict) {
    if (TagValues.isSmallInt(tagValue)) {
      return TagValues.decodeSmallInt(tagValue);
    }
    return TagValues.decodeObject(tagValue, dict);
  }
  static int encodeAnyValue(Object object, Dictionary dict) {
    if (object instanceof Integer /*FIXME and small */) {
      return TagValues.encodeSmallInt((Integer)object);
    }
    return TagValues.encodeObject(object, dict);
  }

  int TRUE = TagValues.encodeSmallInt(1);
  int FALSE = TagValues.encodeSmallInt(0);
}
