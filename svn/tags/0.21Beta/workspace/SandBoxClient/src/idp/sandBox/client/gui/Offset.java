package idp.sandBox.client.gui;

public class Offset
{
	public int left, right;
	public Offset(int left, int right) {this.left = left;this.right = right;}
	
	@Override
	public String toString(){
		return "( " + this.left + ", " + this.right + ")";
	}
}
