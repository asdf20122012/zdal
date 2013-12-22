package com.alipay.zdal.common;

//package com.taobao.tddl.common;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Random;
//import java.util.Map.Entry;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.alibaba.common.lang.io.ByteArrayInputStream;
//import com.taobao.tddl.common.ConfigServerHelper.AbstractDataListener;
//import com.taobao.tddl.common.ConfigServerHelper.DataListener;
//
//public abstract class WeightRandomHelper {
//	
//	protected static Log logger = LogFactory.getLog(WeightRandomHelper.class);
//	
//	public static final int DEFAULT_WEIGHT_NEW_ADD = 0;
//	public static final int DEFAULT_WEIGHT_INIT = 10;
//	
//	protected volatile Map<String, Integer> weightConfigs;
//	protected volatile int[] weights;
//	protected volatile int[] weightAreaEnds;
//	private volatile String[] dataSourceNames;
//	
//	private String appName;
//	
//	protected static  Random random = new Random();
//
//		
//	protected abstract Object subscribeConfig(String appName, DataListener listener);
//	
//	protected void initWeight(Map<String, Integer> config) {
//		final String[] names = dataSourceNames;
//		int[] newWeights = new int[config.size()];
//		for(int i = 0; i < newWeights.length; i++) {
//			String name = names[i];
//			Integer w = config.get(name);
//			if(w == null) {
//				newWeights[i] = DEFAULT_WEIGHT_NEW_ADD;
//			} else {
//				newWeights[i] = w;
//			}
//		}
//		weights = newWeights;
//		weightConfigs = config;
//		weightAreaEnds = genAreaEnds(newWeights);
//	}
//	
//	protected DataListener configListener = new AbstractDataListener() {
//		public void onDataReceive(Object data) {
//			onConfigDataReceive(data);
//		}
//	};
//	
//	protected synchronized void onConfigDataReceive(Object data) {
//		Map<String, Integer> config;
//		try {
//			config = parseData(data);
//		} catch (ConfigException e) {
//			logger.error("从config server中接收到了错误的配置", e);
//			return;
//		}
//		initWeight(config);		
//	}
//	
//	protected <T> int[] initWeightIfNoConfigServer(Map<String, T> dataSources) {
//		int[] newWeight = new int[dataSources.size()];
//		for(int i = 0; i < newWeight.length; i++) {
//			newWeight[i] = DEFAULT_WEIGHT_INIT;
//		}
//		return newWeight;
//	}
//	
//	public synchronized void init() throws ConfigException {
//		init(null);
//	}
//	
//	public synchronized <T> void init(Map<String, T> dataSources) throws ConfigException {
//		//如果已经通过set方法配置了dataSources则不使用config server
//		//这个时候一般是因为已经使用了r-jdbc
//		if(dataSources != null) {
//			weights = initWeightIfNoConfigServer(dataSources);
//			weightAreaEnds = genAreaEnds(weights);
//		} else {
//			Object first = subscribeConfig(getAppName(), configListener);
//			if (first == null) {
//				if(weightConfigs == null) {
//					//throw new ConfigException("[WeightRandomHelper]既得不到configserver配置，也没有本地权重配置");
//				} else {
//					initWeight(weightConfigs);
//				}
//			}
//		}
//	}
//	
//	protected Map<String, Integer> parseData(Object data) throws ConfigException {
//		Properties properties;
//		if(data instanceof Properties) {
//			properties = (Properties) data;
//			logger.warn("fetch properties from config server: "+properties.toString());
//		} else if(data instanceof String) {
//			properties = new Properties();
//			try {
//				properties.load(new ByteArrayInputStream(((String) data).getBytes()));
//			} catch (IOException e) {
//				throw new ConfigException("[WeightRandomHelper]无法解析推送的配置：" + data, e);
//			}
//		} else {
//			throw new ConfigException("[WeightRandomHelper]类型无法识别" + data);
//		}
//		return parsePropertiesConfig(properties);
//	}
//			
//	private Map<String, Integer> parsePropertiesConfig(Map<Object, Object> properties) throws ConfigException {
//		Map<String, Integer> map = new HashMap<String, Integer>(properties.size()); 
//		for(Entry<Object, Object> entrySet : properties.entrySet()) {
//			String key = ((String) entrySet.getKey()).trim();
//			Object objValue = entrySet.getValue();
//			Integer intValue;
//			if(objValue instanceof Integer) {
//				intValue = (Integer)objValue;
//			} else if(objValue instanceof String) {
//				try {
//					intValue = new Integer(((String)objValue).trim());
//				} catch (NumberFormatException e) {
//					throw new ConfigException("parsePropertiesConfig: "
//							+ properties.toString(), e);
//				}
//			} else {
//				throw new ConfigException("Wrong entry value type!");
//			}
//			Integer oldValue = map.get(key);
//			if(oldValue != null) {
//				logger.error("Duplicate weight! the old is "+oldValue+", and the new is "+intValue);
//			}
//			map.put(key, intValue);
//		}
//		return map;
//	}
//	
//	public static interface Tryer {
//		public void tryOne(String name) throws SQLException;
//	}
//	
//	public List<SQLException> retry(int times, Tryer tryer) {
//		List<SQLException> exceptions = new ArrayList<SQLException>();
//		int[] weightAreaEnd = this.weightAreaEnds;
//		int[] weights = this.weights;
//		for (int i = 0; i < times; i++) {
//			String name = WeightRandomHelper.getDataSourceNameByWeightRandom(random, weightAreaEnd, dataSourceNames);
//			try {
//				tryer.tryOne(name);
//				return null;
//			} catch (SQLException e) {
//				exceptions.add(e);
//				logger.error("retry fail", e);
//				for(int k = 0; k < dataSourceNames.length; k++) {
//					if(dataSourceNames[k] == name) {
//						weights = weights.clone();
//						weights[k] = 0;
//						weightAreaEnd = WeightRandomHelper.genAreaEnds(weights);
//						break;
//					}
//				}
//			}
//		}
//		return exceptions;
//	}
//	
//	public List<SQLException> retry(Tryer tryer) {
//		return retry(3, tryer);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void setWeightConfigs(Object config) throws FileNotFoundException, IOException, ConfigException {
//		if (config instanceof String) {
//			String propertiesPath = (String) config;
//			Properties properties = new Properties();
//			InputStream in = getClass().getResourceAsStream(propertiesPath);
//			if (in == null) {
//				in = new FileInputStream(propertiesPath);
//			}
//			properties.load(in);
//			this.weightConfigs = parsePropertiesConfig(properties);
//		} else if(config instanceof Map) {
//			Map<Object, Object> map = (Map<Object, Object>)config;
//			this.weightConfigs = parsePropertiesConfig(map);
//		} else {
//			throw new IllegalArgumentException("unsupport arg type");
//		}
//	}
//	
////	public int[] getWeights(String[] dataSourceNames) {
////		Map<String, Integer> configs = weightConfigs;
////		int[] tmp_weights = new int[dataSourceNames.length];
////		for(int i = 0; i < dataSourceNames.length; i++) {
////			String name = dataSourceNames[i];
////			Integer weight = configs.get(name);
////			if(weight == null) {
////				logger.warn("no weight for datasource: '"+name+"' , use default weight "+DEFAULT_WEIGHT0+"!");
////				weight = DEFAULT_WEIGHT0;
////			}
////			tmp_weights[i] = weight;
////		}
////		return tmp_weights;
////	}
//	
//	public int[] getWeights() {
//		return weights;
//	}
//
//	public int[] getWeightAreaEnd() {
//		return weightAreaEnds;
//	}
//	
//	public Map<String, Integer> getWeightConfigs() {
//		return weightConfigs;
//	}
//
//	public void setAppName(String appName) {
//		this.appName = appName;
//	}
//
//	public String getAppName() {
//		return appName;
//	}
//
//	public void setDataSourceNames(String[] dataSourceNames) {
//		this.dataSourceNames = dataSourceNames;
//	}
//	
//	public <T> void setDataSourceNames(Map<String, T> dataSources) {
//		this.dataSourceNames = dataSources.keySet().toArray(new String[dataSources.size()]);
//	}
//
//	public String[] getDataSourceNames() {
//		return dataSourceNames;
//	}
//
//	public static class ConfigException extends Exception {
//		private static final long serialVersionUID = 1L;
//		
//		public ConfigException() {
//			super();
//		}
//		
//		public ConfigException(String msg) {
//			super(msg);
//		}
//		
//		public ConfigException(String msg, Throwable cause) {
//			super(msg, cause);
//		}
//	}
//	
//	
//	
//	/**
//	 * 模型是这样：
//	 * 假设三个库权重
//	 * 
//	 * 10  9  8
//	 * 那么areaEnds就是
//	 * 10  19 27
//	 * 随机数是0~27之间的一个数
//	 * 
//	 * 分别去上面areaEnds里的元素比。
//	 * 
//	 * 发现随机数小于一个元素了，则表示应该选择这个元素
//	 * 
//	 * @param areaEnds
//	 * @param dataSources
//	 * @return 
//	 * @return
//	 */
//	public static <T> T getDataSourceNameByWeightRandom(Random random, int[] areaEnds, T[] dataSources) {
//		if(areaEnds == null) {
//			logger.warn("areaEnds is null!");
//		} else if(areaEnds.length != dataSources.length) {
//			logger.warn("areaEnds.length != dataSourceNames.length!");
//		} else if(logger.isInforEnable()) {
//			logger.info("area Ends is: "+intArray2String(areaEnds));
//		}
//		if(areaEnds != null && areaEnds.length == dataSources.length) {
//			int sum = areaEnds[areaEnds.length - 1];
//			if(sum == 0) {
//				logger.error("areaEnds: "+intArray2String(areaEnds));
//				return null;
//			}
//			//选择的过程
//			int rand = random.nextInt(sum);
//			for(int i = 0; i < areaEnds.length; i++) {
//				if(rand < areaEnds[i]) {
//					return dataSources[i];
//				}
//			}
//		}
//
//		T ds;
//		for(int i = 0; i < dataSources.length; i++) {
//			ds = dataSources[random.nextInt(dataSources.length)];
//			if(ds != null) {
//				return ds;
//			} else {
//				logger.warn("Get null datasource that may caused by wrong jndi name");
//			}
//		}
//		logger.error("Get all null datasource that may caused by wrong jndi name");
//		return null;
//	}
//	
//	public static int[] genAreaEnds(int[] weights) {
//		if(weights == null) {
//			return null;
//		}
//		int[] areaEnds = new int[weights.length];
//		int sum = 0;
//		for(int i = 0; i < weights.length; i++) {
//			sum += weights[i];
//			areaEnds[i] = sum;
//		}
//		if(logger.isDebugEnabled()) {
//			logger.debug("generate "+intArray2String(areaEnds)+" from "+intArray2String(weights));
//		}
//		if(sum == 0) {
//			logger.warn("generate "+intArray2String(areaEnds)+" from "+intArray2String(weights));
//		}
//		return areaEnds;
//	}
//	
//	public static int[] invalidateWeight(int[] weights, int index) {
//		weights[index] = 0;
//		return weights;
//	}
//	
//	private static String intArray2String(int[] inta) {
//		if(inta == null) {
//			return "null";
//		} else if(inta.length == 0) {
//			return "[]";
//		}
//		StringBuilder sb = new StringBuilder();
//		sb.append("[").append(inta[0]);
//		for(int i = 1; i < inta.length; i++) {
//			sb.append(", ").append(inta[i]);
//		}
//		sb.append("]");
//		return sb.toString();
//	}
//
//}
