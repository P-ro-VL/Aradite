package com.github.tezvn.aradite.language;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

import pdx.mantlecore.item.ItemSerializer;

/**
 * A class represents for a specific language, which contains translated
 * messages.<br>
 * To use languague, it need configuring in {@code config.yml} file with correct
 * locate code (can be seen in {@link Locale #}).
 *
 * @see LanguageManager
 */
public class Language {

    /**
     * The plugin that owns this language.
     */
    private final Plugin plugin;

    /**
     * The locale of the language.
     */
    private final String locale;

    /**
     * A {@link TreeMap} that will store all {@code K-V} pair, which keys are
     * sections and values are translated strings in this language.
     */
    private final TreeMap<String, Element> elements = new TreeMap<>();

    public Language(Plugin plugin, String locale) {
        super();
        this.plugin = plugin;
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    /**
     * Set the {@code section} with {@code value}, which is an already translated
     * string in this language.
     *
     * @param section The section
     * @param value   The translated string
     */
    public void register(String section, Object value) {
        elements.put(section, new Element(value));
    }

    /**
     * Return a specific translated message in {@code section}.
     *
     * @param section The section used to retrieve translated message.
     * @return The translated message.
     */
    public String getString(String section) {
        Element element = this.elements.getOrDefault(section, null);
        return element == null ? "" : element.asString().replaceAll("&", "§");
    }

    public double getDouble(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return 0;
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getInt(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return 0;
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return 0;
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public byte getByte(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return 0;
        try {
            return Byte.parseByte(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public short getShort(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return 0;
        try {
            return Short.parseShort(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getBoolean(String section) {
        String str = getString(section);
        if (str == null || str.isEmpty())
            return false;
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getList(String section) {
        Element element = this.elements.getOrDefault(section, null);
        return element == null ? Lists.newArrayList() : element.asStringList().stream()
                .map(string -> string.replaceAll("&", "§")).collect(Collectors.toList());
    }

    public List<String> getListWithPlaceholders(String section, Placeholder... placeholders) {
        List<String> rawList = getList(section);
        List<String> replacedList = new ArrayList<>();
        outer: for (String string : rawList) {
            String tempString = string;
            inner:
            for (Placeholder placeholder : placeholders) {
                String identifier = "%" + placeholder.getIdentifier() + "%";
                Object replacement = placeholder.getReplacement();

                if (!tempString.contains(identifier)) continue inner;

                if (replacement instanceof String) {
                    String replacementString = (String) replacement;
                    tempString = tempString.replaceAll(identifier, replacementString)
                            .replaceAll("&", "§");
                } else {
                    Class<?> wrapperClass = replacement.getClass();
                    if (Collection.class.isAssignableFrom(wrapperClass)) {
                        Collection<?> collection = (Collection<?>) replacement;

                        replacedList.addAll(collection.stream().map(object -> object.toString()
                                .replaceAll("&", "§")).collect(Collectors.toList()));
                        continue outer;
                    }
                }
            }
            replacedList.add(tempString);
        }
        return replacedList;
    }

    public Map<String, Object> getElements() {
        return Collections.unmodifiableMap(this.elements);
    }

    public Plugin getOwner() {
        return plugin;
    }

    @Override
    public String toString() {
        return "Language{" + "plugin=" + plugin + ", locale='" + locale + '\'' + ", elements=" + elements + '}';
    }

    public static class Element {

        private Object value;

        public Element(Object value) {
            this.value = value;
        }

        public Object asValue() {
            return this.value;
        }

        public String asString() {
            return String.valueOf(asValue());
        }

        public double asDouble() {
            try {
                return Double.parseDouble(asString());
            } catch (Exception e) {
                return 0;
            }
        }

        public int asInt() {
            try {
                return Integer.parseInt(asString());
            } catch (Exception e) {
                return 0;
            }
        }

        public long asLong() {
            try {
                return Long.parseLong(asString());
            } catch (Exception e) {
                return 0;
            }
        }

        public boolean asBoolean() {
            try {
                return Boolean.parseBoolean(asString());
            } catch (Exception e) {
                return false;
            }
        }

        public short asShort() {
            try {
                return Short.parseShort(asString());
            } catch (Exception e) {
                return 0;
            }
        }

        public byte asByte() {
            try {
                return Byte.parseByte(asString());
            } catch (Exception e) {
                return 0;
            }
        }

        public ItemStack asItemStack() {
            try {
                return ItemSerializer.fromBase64(asString());
            } catch (Exception e) {
                return null;
            }
        }

        public List<Object> asList() {
            try {
                if (asValue() instanceof Collection)
                    return (List<Object>) asValue();
                return Lists.newArrayList();
            } catch (Exception e) {
                return null;
            }
        }

        public List<String> asStringList() {
            try {
                if (asValue() instanceof Collection)
                    return ((Collection<?>) asValue()).stream().map(s -> String.valueOf(s).replace("&", "§"))
                            .collect(Collectors.toList());
                return Lists.newArrayList();
            } catch (Exception e) {
                return null;
            }
        }
    }
}
