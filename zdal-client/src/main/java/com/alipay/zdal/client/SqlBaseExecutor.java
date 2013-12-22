package com.alipay.zdal.client;

import java.util.List;

import com.alipay.zdal.client.util.condition.SimpleCondition;



public interface SqlBaseExecutor {
	/**
	 * 查询单个数据的方法。 与ibatis中的用法一致。 如果涉及多库查询则将抛出ibatis产生的DataAccessExpetion异常
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map,Bean或基本类型。
	 * @param rc
	 *            条件表达式，单库单表查询应该使用{@link SimpleCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	Object queryForObject(String statementID, Object parameterObject,
			RouteCondition rc);
	/**
	 * 查询单个数据的方法。 与ibatis中的用法一致。 如果涉及多库查询则将抛出ibatis产生的DataAccessExpetion异常
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map,Bean或基本类型。
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	Object queryForObject(String statementID, Object parameterObject);
	/**
	 * 查询单个数据的方法，可做多表数值型数据累加
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param param
	 *            绑定变量参数，必须为Map,Bean或基本类型。
	 * @param isExistsQuit
	 *            是否将数值型数据在遍历多库的情况下累加
	 * @param rc
	 *            条件表达式，单库单表查询应该使用{@link SimpleCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	Object queryForObject(String statementID, Object param,
			boolean isExistsQuit, RouteCondition rc);
	/**
	 * 查询单个数据的方法，可做多表数值型数据累加
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param param
	 *            绑定变量参数，必须为Map,Bean或基本类型。
	 * @param isExistsQuit
	 *            是否将数值型数据在遍历多库的情况下累加
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	Object queryForObject(String statementID, Object param,
			boolean isExistsQuit);
	/**
	 * 查询多个数据的方法，一期只支持单库多表的查询，如果涉及多表查询则将所有查询结果合并后返回。
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param rc
	 *            条件表达式，单库单表查询应该使用{@link SimpleCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	List<Object> queryForList(String statementID, Object parameterObject,
			RouteCondition rc);
	/**
	 * 查询多个数据的方法，一期只支持单库多表的查询，如果涉及多表查询则将所有查询结果合并后返回。
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型。
	 */
	List<Object> queryForList(String statementID, Object parameterObject);
	/**
	 * 查询多个数据的方法，一期只支持单库多表的查询，如果涉及多表查询则将所有查询结果合并后返回。
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param isExistQuit
	 *            是否将数值型数据在遍历多库的情况下累加
	 * @param rc
	 *            条件表达式，单库单表查询应该使用{@link SimpleCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForList(String statementID, Object parameterObject,
			boolean isExistQuit, RouteCondition rc);
	/**
	 * 查询多个数据的方法，一期只支持单库多表的查询，如果涉及多表查询则将所有查询结果合并后返回。
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param isExistQuit
	 *            是否将数值型数据在遍历多库的情况下累加
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForList(String statementID, Object parameterObject,
			boolean isExistQuit);
	/**
	 * 排序查询,但目前只支持单库多表，可以排序分页。一期中就是
	 * {@link #queryForMergeSortTables(String, Object, RouteCondition)} 的重载函数
	 * 
	 * @param statementID
	 *            . ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param rc
	 *            条件表达式，单库多表查询应该使用{@link MergeSortTablesCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForMergeSortList(String statementID,
			Object parameterObject, RouteCondition rc);
	/**
	 * 排序查询,但目前只支持单库多表，可以排序分页。一期中就是
	 * {@link #queryForMergeSortTables(String, Object, RouteCondition)} 的重载函数
	 * 
	 * @param statementID
	 *            . ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForMergeSortList(String statementID,
			Object parameterObject);
	/**
	 * 排序查询,但只支持单库多表查询，表与表之间的大小关系是在插入数据时就已经确定好的，如表1内所有数据永远小于表2,以此类推，可以排序分页。
	 * 参数必须是{@link MergeSortTablesCondition}
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param rc
	 *            条件表达式，单库多表查询应该使用{@link MergeSortTablesCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForMergeSortTables(String statementID,
			Object parameterObject);
	/**
	 * 排序查询,但只支持单库多表查询，表与表之间的大小关系是在插入数据时就已经确定好的，如表1内所有数据永远小于表2,以此类推，可以排序分页。
	 * 参数必须是{@link MergeSortTablesCondition}
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，<b>由于Bean或基本类型不能动态添加属性，因此如果涉及范围查询请不要使用Bean或基本类型作为参数
	 *            </b>
	 * @param rc
	 *            条件表达式，单库多表查询应该使用{@link MergeSortTablesCondition}
	 * @return ibatic 配置文件中的resultMap中对应的对象或基本类型的列表。
	 */
	List<Object> queryForMergeSortTables(String statementID,
			Object parameterObject, RouteCondition rc);

	/**
	 * 更新数据库，删除会报错弹出。只能进行单库单表更新
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，Bean或基本类型。
	 * @param rc
	 *            条件表达式，单库单表应该使用{@link SimpleCondition}
	 * @return 更新影响的行数
	 */
	int update(String statementID, Object parameterObject, RouteCondition rc);
	/**
	 * 更新数据库，删除会报错弹出。只能进行单库单表更新
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，Bean或基本类型。
	 * @return 更新影响的行数
	 */
	int update(String statementID, Object parameterObject);
	/**
	 * 插入一条记录
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，Bean或基本类型。
	 * @param rc
	 *            条件表达式，单库单表应该使用{@link SimpleCondition}
	 * @return 与ibatis返回一致
	 */
	Object insert(String statementID, Object parameterObject, RouteCondition rc);
	/**
	 * 插入一条记录
	 * 
	 * @param statementID
	 *            ibatis 配置文件中的id属性。
	 * @param parameterObject
	 *            绑定变量参数，必须为Map，Bean或基本类型。
	 * @return 与ibatis返回一致
	 */
	Object insert(String statementID, Object parameterObject);

}
