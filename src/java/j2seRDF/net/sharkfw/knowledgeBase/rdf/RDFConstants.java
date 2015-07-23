package net.sharkfw.knowledgeBase.rdf;

/**
 * 
 * @author Barret dfe
 *
 */
public final class RDFConstants {
	
	/************************************************* 
	 *  Model (set) names
	 ************************************************/
	
	public static final String ST_MODEL_NAME = "Topic";
	
	public static final String PEER_MODEL_NAME = "Peer";
	
	public static final String TIME_MODEL_NAME = "Time";
	
	public static final String SPATIAL_MODEL_NAME = "Spatial";
	
	public static final String 	CONTEXT_POINT_MODEL_NAME = "Context Points";
	
	public static final String 	INFORMATION_MODEL_NAME = "Information";
	
	public static final String SEMANTIC_NET_MODEL_NAME = "Semantic Net";
	
	
	/************************************************* 
	 *  Predicate names for tags
	 ************************************************/
	
	public static final String SEMANTIC_TAG_PREDICATE = "http://www.sharksystem.net/Topic/describes";
	
	public static final String PEER_TAG_PREDICATE = "http://www.sharksystem.net/Peer/contactableby";
	
	public static final String PEER_TAG_ADDRESS_PREDICATE = "http://www.sharksystem.net/Peer/contacts";
	
	public static final String SPATIAL_TAG_PREDICATE = "http://www.sharksystem.net/SharkGeometry";
	
	public static final String SPATIAL_TAG_EWKT = "http://www.sharksystem.net/SharkGeometry/ewkt";
	
	public static final String SPATIAL_TAG_SRS = "http://www.sharksystem.net/SharkGeometry/srs";
	
	public static final String SPATIAL_TAG_WKT = "http://www.sharksystem.net/SharkGeometry/wkt";
	
	public static final String TIME_TAG = "http://www.sharksystem.net/time";
	
	public static final String TIME_TAG_FROM = "http://www.sharksystem.net/time/from";
	
	public static final String TIME_TAG_DURATION = "http://www.sharksystem.net/time/duration";
	
	
	/************************************************* 
	 *  names for Context Point
	 ************************************************/
	
	public static final String CONTEXT_POINT_SUBJECT = "http://www.sharksystem.net/ContextPoint";
	
	public static final String CONTEXT_POINT_PREDICATE_TOPIC = "http://www.sharksystem.net/ContextPoint/Topic";
	
	public static final String CONTEXT_POINT_PREDICATE_ORIGINATOR = "http://www.sharksystem.net/ContextPoint/Originator";
	
	public static final String CONTEXT_POINT_PREDICATE_PEER = "http://www.sharksystem.net/ContextPoint/Peer";
	
	public static final String CONTEXT_POINT_PREDICATE_REMOTE_PEER = "http://www.sharksystem.net/ContextPoint/RemotePeer";
	
	public static final String CONTEXT_POINT_PREDICATE_LOCATION = "http://www.sharksystem.net/ContextPoint/Location";
	
	public static final String CONTEXT_POINT_PREDICATE_TIME = "http://www.sharksystem.net/ContextPoint/Time";
	
	public static final String CONTEXT_POINT_PREDICATE_DIRECTION = "http://www.sharksystem.net/ContextPoint/Direction";
	
	/************************************************* 
	 *  Object names for tags
	 ************************************************/
	
	public static final String PEER_TAG_OBJECT_NAME_ADDRESS = "/address";
	
	public static final String SPATIAL_TAG_OBJECT_NAME_GEOMETRY = "/geometry";
	
	/************************************************* 
	 *  KB various tags
	 ************************************************/
	
	public static final String KB_OWNER = "http://www.sharksystem.net/KnowledgeBase/Owner";
	
	public static final String KB_OWNER_PREDICATE = KB_OWNER + "/owns";
	
	
	/************************************************* 
	 *  KB names for Information
	 ************************************************/
	
	public static final String INFORMATION_SUBJECT = "http://www.sharksystem.net/ContextPoint/Information";
	
	public static final String INFORMATION_PREDICATE = "http://www.sharksystem.net/ContextPoint/Information/located";
	
	public static final String INFORMATION_PATH = "src/java/j2seRDF/net/sharkfw/knowledgeBase/rdf/information/";	

}
