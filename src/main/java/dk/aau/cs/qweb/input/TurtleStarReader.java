package dk.aau.cs.qweb.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;

import dk.aau.cs.qweb.model.NodeFactoryStar;

public class TurtleStarReader {

	private Model model;
	private Map<String,String> prefix;
	
	private enum State {
		NEW,SEMICOLON,COLON, SUBJECT_ONLY,SUBJECT_PREDICATE_ONLY
	}

	public TurtleStarReader(Model model) {
		this.model = model;
		prefix = new HashMap<String,String>();
	}
	
	public void read(File file) throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			Graph g = model.getGraph();
			String subject = null;
			String predicate = null;
			String object = null;
			State state = State.NEW;
			
		    for(String lineRaw; (lineRaw = br.readLine()) != null; ) {
		    	boolean isLineParsed = false;
		    	lineRaw = lineRaw.trim();
		    	if (lineRaw.startsWith("@")) {
					addPrefix(lineRaw);
					isLineParsed = true;
				} else if (lineRaw.equals("")) {
					isLineParsed = true;
					//Empty line, do nothing
				}
		    	
				if (!isLineParsed) {
		    		for (String line: splitTriplePatternStatements(lineRaw) ) {
				    	line = line.trim();
						List<String> split = spitLine(line);
						if (state == State.NEW) {
							subject = null;
							predicate = null;
							object = null;
							
							if (split.size() == 4) { 
								//Full line read.
								subject = split.get(0);
								predicate = split.get(1);
								object = split.get(2);
								state = setState(split.get(3));
								
								g.add(createTriple(subject,predicate,object));
							} else {
								if (split.size() == 1) {
									subject = split.get(0);
									state = State.SUBJECT_ONLY;
								} if (split.size() == 2) {
									predicate = split.get(1);
									state = State.SUBJECT_PREDICATE_ONLY;
								} if (split.size() == 3) {
									throw new IllegalArgumentException("Line: '"+line+ "' is missing a closing char: .,;" );
								}
	//							else {
	//								throw new IllegalStateException("Did not understand line: "+line);
	//							}
							}
						} else if (state == State.SUBJECT_ONLY) {
							if (split.size() == 1) {
								predicate = split.get(0);
								state = State.SUBJECT_PREDICATE_ONLY;
							} else if (split.size() == 2) {
								throw new IllegalArgumentException("Line: '"+line+ "' is missing a closing char: .,;" );
							} else if (split.size() == 3) {
								predicate = split.get(0);
								object = split.get(1);
								state = setState(split.get(2));
								g.add(createTriple(subject,predicate,object));
							} else {
								throw new IllegalArgumentException("Line: '"+line+ "' is missing a closing char: .,;" );
							}
						} else if (state == State.SUBJECT_PREDICATE_ONLY) {
							if (split.size() == 2) {
								object = split.get(0);
								state = setState(split.get(1));
								g.add(createTriple(subject,predicate,object));
							} else {
								throw new IllegalArgumentException("Line: '"+line+ "' is missing a closing char: .,;" );
							}
						} else if (state == State.SEMICOLON) {
							predicate = split.get(0);
							object = split.get(1);
							state = setState(split.get(2));
							g.add(createTriple(subject,predicate,object));
						} else if (state == State.COLON) {
							object = split.get(0);
							state = setState(split.get(1));
							g.add(createTriple(subject, predicate, object));
						} else {
							throw new IllegalStateException("state "+state +" is not known");
						}
					}
				}
			}
		}
	}

	private List<String> splitTriplePatternStatements(String lineRaw) {
		boolean isQoute = false;
		boolean isResource = false;
		lineRaw = lineRaw.trim();
		List<String> triplePatterns = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (char c : lineRaw.toCharArray()) {
			if (c == '"') {
				if (isQoute) {
					isQoute = false;
				} else {
					isQoute = true;
				}
				sb.append(c);
			} else if (c == '<') {
				if (!isResource) {
					isResource = true;
				}
				sb.append(c);
			}else if (c == '>') {
				if (isResource) {
					isResource = false;
				}
				sb.append(c);
			} else if (c == ';' || c == ',' || c == '.' ){
				if (!isQoute && !isResource) {
					sb.append(c);
					triplePatterns.add(sb.toString());
					sb = new StringBuilder();
				}
			} else if (c == '#') {
				if (!isResource && !isQoute) {
					return triplePatterns;
				} else {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		if (!sb.toString().equals("")) {
			triplePatterns.add(sb.toString());
		}
		
		return triplePatterns;
	}

	private Triple createTriple(String subject, String predicate, String object) {
		Node subjectNode = NodeFactoryStar.createURI(expandPrefix(subject));
		Node predicateNode = NodeFactoryStar.createURI(expandPrefix(predicate));
		Node objectNode = createObject(object);
		return new Triple(subjectNode,predicateNode,objectNode);
	}

	private String expandPrefix(String url) {
		if (url.contains(":")) {
			String[] split = url.split(":");
			String prefixString = prefix.get(split[0]);
			
			return prefixString+split[1];
		} else if (url.contains("#")){
			String[] split = trimURL(url).split("#");
			
			return trimURL(prefix.get("")+split[1]);
		} else if (url.equals("a")) {
			return "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
		} else {
			return url;
		}
	}

	private Node createObject(String string) {
		Node node;
		if (string.startsWith("\"")) {
			node = NodeFactoryStar.createLiteral(string);
		} else {
			node = NodeFactoryStar.createURI(expandPrefix(string));
		}
		return node;
	}

	private State setState(String string) {
		if (string.equals(".")) {
			return State.NEW;
		} else if (string.equals(";")) {
			return State.SEMICOLON;
		} else if (string.equals(",")) {
			return State.COLON;
		}
		throw new IllegalStateException("Unknown end of line char "+string);
	}

	private List<String> spitLine(String line) {
		List<String> split = new ArrayList<String>();
		boolean isQuoute = false;
		StringBuilder sb = new StringBuilder();
		
		for (char character : line.toCharArray()) {
			if (character == '"') {
				if (isQuoute) {
					isQuoute = false;
					sb.append(character);
				} else {
					isQuoute = true;
					sb.append(character);
				}
			} else if (character == ' ') {
				if (isQuoute) {
					sb.append(character);
				} else {
					split.add(sb.toString());
					sb = new StringBuilder();
				}
			} else if (character == '.' || character == ',' || character == ';') {
				if (!sb.toString().equals("")) {
					split.add(sb.toString());
					sb = new StringBuilder();
				}
				sb.append(character);
				break;
			} else {
				sb.append(character);
			}
		}
		split.add(sb.toString());
		return split;
	}

	private void addPrefix(String line) {
		// @base <http://example.org/> .
		// @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
		
		String[] split = line.split(" ");
		if (split[0].equals("@base")) {
			prefix.put("",trimURL(split[1])); //remove <>
		} else if (split[0].equals("@prefix")) {
			prefix.put(split[1].substring(0, split[1].length()-1),trimURL(split[2])); //remove <>
		}
	}

	private String trimURL(String string) {
		if (string.startsWith("<")) {
			string = string.substring(1);
		}
		if (string.endsWith(".")) {
			string = string.substring(0,string.length()-2);
		} else if (string.endsWith(">")) {
			string = string.substring(0,string.length()-1);
		}
		return string;
	}
}
