/**
 *
 */
package net.sharkfw.knowledgeBase.sql;

/**
 * An exception that indicates there was an error with SQL parsing or execution.
 *
 * Supported by <a href="http://www.webXells.com">webXells GmbH</a>
 *
 * @author marko johann
 * @date 30.03.2011
 */
public class SharkSQLException extends RuntimeException {

	private static final long serialVersionUID = -8583094886804950468L;
	private final Throwable causeThrowable;

	/**
	 * This constructor takes a {@link Throwable} which responsible for the error
	 *
	 * @param throwable
	 */
	public SharkSQLException(final Throwable throwable) {
		initCause(throwable);
		causeThrowable = throwable;
	}

	// ========================================================================

	@Override
	public String getMessage() {
		return "there is an error by " + causeThrowable.getClass().getSimpleName();
	}
}
