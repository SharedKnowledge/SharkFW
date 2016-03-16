package net.sharkfw.knowledgeBase;

/**
 * <p>An <code>AssociatedSemanticTag</code> that unites the characteristics of regular tags
 * and peer-tags.</p>
 *
 * <p>Peers can be organized in different ways. One possible application of this
 * would be organize peers in <em>groups</em>. For this purpose the usage of the
 * <code>SemanticNet.SUBASSOC</code> and <code>SemanticNet.SUPERASSOC</code>
 * can be used. <br />
 * An organization could be the root tag for all its members, which in turn
 * are subtags of the organization. Thus {@link ContextPoint}s can be created,
 * which are valid for all members of a certain group. The ContextPoint need
 * only have the group-peer set as remotepeer. When organizing peers into groups,
 * please use the <code>KnowledgePort.HIDDEN</code> tag, if the organization itself
 * cannot be addressed by network. Otherwise the organization will become part
 * of a created interest (or Knowledge), and thus be addressed, when the KEPMessage is sent.</p>
 *
 * @see net.sharkfw.knowledgeBase.AssociatedSemanticTag
 * @see net.sharkfw.knowledgeBase.PeerSemanticTag
 * 
 * @author mfi
 */
public interface PeerSNSemanticTag extends SNSemanticTag, PeerSemanticTag, PeerTXSemanticTag{
  
}
