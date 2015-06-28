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
	
	
	/************************************************* 
	 *  Predicate names for tags
	 ************************************************/
	
	public static final String SEMANTIC_TAG_PREDICATE = "http://www.dict.cc/?s=describe";
	
	public static final String PEER_TAG_PREDICATE = "http://www.dict.cc/?s=contactableby";
	
	public static final String PEER_TAG_ADDRESS_PREDICATE = "http://www.dict.cc/?s=contacts";
	
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
}
