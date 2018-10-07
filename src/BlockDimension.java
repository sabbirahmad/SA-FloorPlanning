
public class BlockDimension {
	int width;
	int height;
	int area;
	public BlockDimension(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		area = width*height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	
	public String toString(){
		return "width: "+width+"; height: "+height;
	}
	
	public int dominates(BlockDimension d){
		if(this.getWidth() <= d.getWidth() && this.getHeight() <= d.getHeight()){
			return 1;
		}
		else if(this.getWidth() >= d.getWidth() && this.getHeight() >= d.getHeight()){
			return -1;
		}
		return 0;			
	}
	
}
