package index;

import java.io.Serializable;


public class Index implements Serializable{
	private String FilePath;
	private int LineNum;
	private int FileNum;

	private static final long serialVersionUID = 8059334008794075409L;
	
	public Index(String FilePath,int LineNum,int FileNum) {
		this.FilePath = FilePath;
        this.LineNum = LineNum;
        this.FileNum = FileNum;
	}
	

	public Index() {
		// TODO 自动生成的构造函数存根
	}


	public int getFileNum() {
        return FileNum;
    }

    public String getFilePath() {
        return FilePath;
    }


    public int getLineNum() {
        return LineNum;
    }
	
	
}
