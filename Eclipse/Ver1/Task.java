package Ver1;

public class Task {
	public int row;
	public int col;
	public boolean typ;
	
	public Task() {
		row=0;col=0;typ=false;
	}
	public Task(int row,int col,boolean typ) {
		this.row=row;this.col=col;this.typ=typ;
	}
	public String toString() {
		String str = null;
		if(typ==true) str="in,"+row+","+col;
		else str="out,"+row+","+col;
		return str;
	}
}
