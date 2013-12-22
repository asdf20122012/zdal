package com.alipay.zdal.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 *用于将String包装为Clob对象以被系统识别。否则无法直接将clob指定为系统对象。
 * @author shenxun
 *
 */
public class StringClobWrapper implements java.sql.Clob{
	String str=null;
	public StringClobWrapper(String str) {
		this.str=str;
	}
	public void free() throws SQLException {
		str=null;
	}

	public InputStream getAsciiStream() throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public Reader getCharacterStream() throws SQLException {
		if(str!=null){
			return new StringReader(str);
		}
		else{
			return null;
		}
	}

	public Reader getCharacterStream(long pos, long length) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public String getSubString(long pos, int length) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	/* (non-Javadoc)
	 * @see java.sql.Clob#length()
	 */
	public long length() throws SQLException {
		if(str!=null){
			return str.length();
		}
		else{
			return 0;
		}
	}

	public long position(String searchstr, long start) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public long position(Clob searchstr, long start) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public OutputStream setAsciiStream(long pos) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public Writer setCharacterStream(long pos) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public int setString(long pos, String str) throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public int setString(long pos, String str, int offset, int len)
			throws SQLException {
		throw new SQLException("不支持的操作");
	}

	public void truncate(long len) throws SQLException {
		throw new SQLException("不支持的操作");
	}

}
