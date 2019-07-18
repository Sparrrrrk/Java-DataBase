package index;

import java.io.Serializable;
import java.util.TreeMap;

public class IndexTree implements Serializable {

	private TreeMap<String, Index> treeMap;//红黑树对应的map，自带顺序
	private static final long serialVersionUID = -4490230638026878658L;
	public IndexTree()
	{
		treeMap = new TreeMap<>();
	}
	
	public TreeMap<String, Index> getTreeMap()
	{
		return treeMap;
	}
	
	public void setTreeMap(TreeMap<String, Index> treeMap)
	{
		this.treeMap = treeMap;
	}
	
	public void putIndex(String indexKey,String FilePath,int LineNum,int FileNum)
	{
		Index index = new Index(FilePath, LineNum , FileNum);
		treeMap.put(indexKey,index);
	}
	
}
