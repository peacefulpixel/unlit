package me.ppixel.unlit.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UnlitXMLElement {
    public String type;
    public Map<String, String> parameters = new HashMap<>();
    public List<UnlitXMLElement> children = new LinkedList<>();
}
