package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.StringHolder;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	Character element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	Node parent; // You may want this field.
	// Feel free to add other fields that you find useful
	int numRotations;
	EditTree treeForRootUpdate;
	// You will probably want to add several other methods
	public Node(Character c, EditTree tree) {
		this.treeForRootUpdate = tree;
		this.element = c;
		this.numRotations = 0;
		this.parent = EditTree.NULL_NODE;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.balance = Code.SAME;
	}

	public Node(Character c, Node parent, EditTree tree) {
		this.element = c;
		this.treeForRootUpdate = tree;
		this.numRotations = 0;
		this.parent = parent;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.balance = Code.SAME;
	}
	
	public Node(Character c, EditTree tree, int rank){
		this.element = c;
		this.rank = rank;
		this.treeForRootUpdate = tree;
		this.numRotations = 0;
		this.parent = EditTree.NULL_NODE;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.balance = Code.SAME;
	}
	
	public Node(Character c, EditTree tree, int rank, Code balance, Node parent){
		this.element = c;
		this.rank = rank;
		this.treeForRootUpdate = tree;
		this.numRotations = 0;
		this.parent = parent;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.balance = balance;
	}

	// For the following methods, you should fill in the details so that they
	// work correctly
//	public int height() {
//		if (this == EditTree.NULL_NODE) {
//			return 0;
//		}
//		return 1 + Math.max(this.left.height(), this.right.height());
//	}
	
	public int height(){
		if(this == EditTree.NULL_NODE){
			return 0;
		}
		switch(this.balance){
			case RIGHT:
				return 1 + this.right.height();
			case LEFT:
				return 1 + this.left.height();
			case SAME:
				return 1 + this.left.height();
			default:
				return 1 + this.left.height();
		}		
	}
	
	

	public void toArrayList(ArrayList<Character> toReturn) {
		if (this == EditTree.NULL_NODE) {
			return;
		}
		this.left.toArrayList(toReturn);
		toReturn.add(this.element);
		this.right.toArrayList(toReturn);
	}

	public void add(char ch) {
		if (this.right == EditTree.NULL_NODE) {
			this.right = new Node(ch, this, this.treeForRootUpdate);
			this.updateBalance('r');
		} else {
			this.right.add(ch);
		}
	}

	public void updateBalance(char balance) {
		if (this == EditTree.NULL_NODE) {
			return;
		}
		boolean parentDirection;
		if(this.parent.left == this){
			parentDirection = false;
		}
		else{
			parentDirection = true;
		}
		if (balance == 'r') {
			switch (this.balance) {
			case RIGHT:
				if(this.right.balance == Code.RIGHT /*|| this.right.balance == Code.SAME*/){
					this.rotateLeft();
				}
				else if(this.right.balance == Code.LEFT){
					int bSlant = 0;//left is -1, right is 1
					if(this.right.left.balance == Code.LEFT){
						bSlant = -1;
					}
					else if(this.right.left.balance == Code.RIGHT){
						bSlant = 1;
					}
					else{
						bSlant = 0;
					}
					this.right.rotateRight();
					this.rotateLeft();
					if(bSlant == 1){
						this.balance = Code.LEFT;
					}
					else if(bSlant == -1){
						this.parent.right.balance = Code.RIGHT;
					}
				}
				break;
			case LEFT:
				this.balance = Code.SAME;
				break;
			case SAME:
				this.balance = Code.RIGHT;
				if(parentDirection){
					this.parent.updateBalance(balance);
				}
				else{
					this.parent.updateBalance('l');
				}
				break;
			}
		} else if(balance == 'l'){
			switch (this.balance) {
			case RIGHT:
				this.balance = Code.SAME;
				break;
			case LEFT:
				if(this.left.balance == Code.LEFT /*|| this.left.balance == Code.SAME*/){
					this.rotateRight();
				}
				else if(this.left.balance == Code.RIGHT){
					int bSlant = 0;//left is -1, right is 1
					if(this.left.right.balance == Code.LEFT){
						bSlant =-1;
					}
					else if(this.left.right.balance == Code.RIGHT){
						bSlant = 1;
					}
					else{
						bSlant = 0;
					}
					this.left.rotateLeft();
					this.rotateRight();
					if(bSlant == -1){
						this.balance = Code.RIGHT;
					}
					else if(bSlant == 1){
						this.parent.left.balance = Code.LEFT;
					}
				}
				break;
			case SAME:
				this.balance = Code.LEFT;
				if(parentDirection){
					this.parent.updateBalance('r');
				}
				else{
					this.parent.updateBalance(balance);
				}
				break;
			}
		}
	}

	public void rotateLeft() {
		Node A = this;
		Node B = this.right;
		//Code bBalance = B.balance;
		Node T3, T2;
//		if(bBalance == Code.RIGHT){
//			T3 = B.left;
//			T2 = B.right;
//		}
		//else{
			T3 = B.right;
			T2 = B.left;	
		//}
		Node T1 = A.left;
		if(this.parent != EditTree.NULL_NODE){
			boolean parentSide; // true if right, false if left
			if(A.parent.left == A){
				parentSide = false;
			}
			else{
				parentSide = true;
			}
			Node parent = A.parent;
			B.left = A;
			B.right = T3;
			A.parent = B;
			B.parent = parent;
			A.left = T1;
			A.right = T2;
			if(parentSide){
				parent.right = B;
			}
			else{
				parent.left = B;
			}
			B.rank = A.rank + B.rank + 1;
			B.balance = Code.SAME;
			A.balance = Code.SAME;
			this.updateRotationsNotAtRoot();
		}
		else{
			B.left = A;
			B.right = T3;
			A.parent = B;
			A.left = T1;
			A.right = T2;
			B.parent = EditTree.NULL_NODE;
			this.treeForRootUpdate.updateRoot(B);
			B.numRotations = A.numRotations + 1;
			A.numRotations = 0;
			B.rank = A.rank + B.rank + 1;
			B.balance = Code.SAME;
			A.balance = Code.SAME;
		}
	}
	public void rotateRight(){
		Node A = this;
		Node B = this.left;
		Node T3, T2;
		T3 = B.left;
		T2 = B.right;
		Node T1 = A.right;
		if(this.parent != EditTree.NULL_NODE){
			boolean parentSide; // true if right, false if left
			if(A.parent.left == A){
				parentSide = false;
			}
			else{
				parentSide = true;
			}
			Node parent = A.parent;
			B.right = A;
			B.left = T3;
			A.parent = B;
			B.parent = parent;
			A.right = T1;
			A.left = T2;
			if(parentSide){
				parent.right = B;
			}
			else{
				parent.left = B;
			}			
			A.rank = T2.size();
			B.balance = Code.SAME;
			A.balance = Code.SAME;
			this.updateRotationsNotAtRoot();
		}
		else{
			B.right = A;
			B.left = T3;
			A.parent = B;
			B.parent = EditTree.NULL_NODE;
			A.right = T1;
			A.left = T2;
			this.treeForRootUpdate.updateRoot(B);
			B.numRotations = A.numRotations + 1;
			A.numRotations = 0;
			A.rank = T2.size();
			A.balance = Code.SAME;
			B.balance = Code.SAME;
		}
		
	}
	private void updateRotationsNotAtRoot(){
		if(this.parent == EditTree.NULL_NODE){
			this.numRotations ++;
			return;
		}
		this.parent.updateRotationsNotAtRoot();
	}

	public void toDebugString(ArrayList<String> toReturn) {
		if (this == EditTree.NULL_NODE) {
			return;
		}
		toReturn.add("" + this.element + this.rank + this.balance.toString());
		this.left.toDebugString(toReturn);
		this.right.toDebugString(toReturn);
	}

	public void add(char ch, int pos) {
		if (this.rank >= pos) {
			this.rank++;
			if (this.left != EditTree.NULL_NODE) {
				this.left.add(ch, pos);
			} else {
				this.left = new Node(ch, this, this.treeForRootUpdate);
				this.updateBalance('l');
			}
		} else {
			if (this.right != EditTree.NULL_NODE) {
				this.right.add(ch, pos - (this.rank + 1));
			} else {
				this.right = new Node(ch, this, this.treeForRootUpdate);
				this.updateBalance('r');
			}
		}
	}

	public char get(int pos) {
		if(this.rank == pos){
			return this.element;
		}
		else if(this.rank > pos){
			return this.left.get(pos);
		}
		else{
			return this.right.get(pos - (this.rank + 1));
		}
	}
	
	public int size(){
		if(this == EditTree.NULL_NODE){
			return 0;
		}
		return 1 + this.rank + this.right.size();
	}
//	public int size(){
//		if(this == EditTree.NULL_NODE){
//			return 0;
//		}
//		return 1 + this.left.size() + this.right.size();
//	}
	
	public int numChildren(){
		if(this.left != EditTree.NULL_NODE && this.right != EditTree.NULL_NODE){
			return 2;
		}
		else if(this.left != EditTree.NULL_NODE && this.right == EditTree.NULL_NODE){
			return -1;
		}
		else if(this.left == EditTree.NULL_NODE && this.right != EditTree.NULL_NODE){
			return 1;
		}
		else{
			return 0;
		}
	}

	public char delete(int pos) {
		char toReturn;
		if(this.rank == pos){
			toReturn = this.element;
			if(this.parent == EditTree.NULL_NODE){
				int numChildren = this.numChildren();
				if(numChildren == 2){
					if (this.right.numChildren()==0) {
						this.element = this.right.element;
						this.right = EditTree.NULL_NODE;
						this.updateBalanceDelete('l', 0);
					}
					else if(this.right.numChildren() == 1){
						this.right.left = this.left;
						this.left.parent = this.right;
						this.right.rank = this.rank;
						this.right.parent = EditTree.NULL_NODE;
						this.treeForRootUpdate.updateRoot(this.right);
						this.right.updateBalanceDelete('l', 0);
					}
					else{
						this.element = this.right.recurseLeft();
					}
				}
				else if(numChildren == 1){//right child
					this.treeForRootUpdate.updateRoot(this.right);
					this.right.numRotations = this.numRotations;
					this.right.parent = EditTree.NULL_NODE;					
				}
				else if(numChildren == -1){//left child
					this.treeForRootUpdate.updateRoot(this.left);
					this.left.numRotations = this.numRotations;
					this.left.parent = EditTree.NULL_NODE;
				}
				else{
					this.treeForRootUpdate.updateRoot(EditTree.NULL_NODE);
				}
			}
			else{
				boolean parentDirection;//false is left, true is right
				Code parentBalance = this.parent.balance;
				if(this.parent.left == this){
					parentDirection = false;
				}
				else{
					parentDirection = true;
				}
				int numChildren = this.numChildren();
				if(numChildren == 2){
					if (this.right.numChildren()==0) {
						this.element = this.right.element;
						this.right = EditTree.NULL_NODE;
						this.updateBalanceDelete('l', 0);
					}
					else if(this.right.numChildren() == 1){
						this.right.left = this.left;
						this.left.parent = this.right;
						this.right.rank = this.rank;
						this.right.parent = this.parent;
						if(parentDirection){
							this.parent.right = this.right;
						}
						else{
							this.parent.left = this.right;
						}
						this.right.updateBalanceDelete('l', 0);						
					}
					else{
						this.element = this.right.recurseLeft();
					}
				}
				else if(numChildren == 1){
					if(parentDirection){
						this.parent.right = this.right;
						this.right.parent = this.parent;
						this.parent.updateBalanceDelete('l', 0);
					}
					else{
						this.parent.left = this.right;
						this.right.parent = this.parent;
						this.parent.updateBalanceDelete('r', 0);
					}
				}
				else if(numChildren == -1){
					if(parentDirection){
						this.parent.right = this.left;
						this.left.parent = this.parent;
						this.parent.updateBalanceDelete('l', 0);
					}
					else{
						this.parent.left = this.left;
						this.left.parent = this.parent;
						this.parent.updateBalanceDelete('r', 0);
					}
				}
				else{
					if(parentDirection){
						this.parent.right = EditTree.NULL_NODE;
						this.parent.updateBalanceDelete('l', 0);
					}
					else{
						this.parent.left = EditTree.NULL_NODE;
						this.parent.updateBalanceDelete('r', 0);
					}
				}
			}
			return toReturn;
		}
		else if(this.rank > pos){
			this.rank --;
			return this.left.delete(pos);
		}
		else{
			return this.right.delete(pos - (this.rank + 1));
		}
	}

//	public Character recurseRight() {
//		int numChildren = this.numChildren();
//		if (numChildren==2) {
//			return this.right.recurseRight();
//		}else if (numChildren == 1) {//right only
//			this.updateBalanceDelete('l', 0);
//			Character temp = this.right.element;
//			this.right = EditTree.NULL_NODE;
//			return temp;
//		}else if (numChildren == -1) {//left only
//			this.parent.right = this.left;
//			this.left = EditTree.NULL_NODE;
//			this.parent.updateBalanceDelete('l', 0);
//			return this.element;
//		}else{
//			this.parent.right = EditTree.NULL_NODE;
//			this.parent.updateBalanceDelete('l', 0);
//			return this.element;
//		}		
//	}
	
	private Character recurseLeft() {
		int numChildren = this.numChildren();
		if (numChildren==2) {
			this.rank--;
			return this.left.recurseLeft();
		}else if (numChildren == -1) {//left only
			Character temp = this.left.element;
			this.updateBalanceDelete('r', 0);
			this.left = EditTree.NULL_NODE;
			this.rank--;
			return temp;
		}else if (numChildren == 1) {//right only
			this.parent.left = this.right;
			this.right = EditTree.NULL_NODE;
			return this.element;
		}else{
			this.parent.updateBalanceDelete('r', 0);
			this.parent.left = EditTree.NULL_NODE;
			return this.element;
		}
		
	}
	
	public void updateBalanceDelete(char bal, int numRotations){
		if(this == EditTree.NULL_NODE){
			return;
		}
		boolean parentDirection = false;//left is false, right is true
		if(this.parent != EditTree.NULL_NODE){
			if(this.parent.left == this){
				parentDirection = false;
			}
			else{
				parentDirection = true;
			}
		}
		if(bal == 'r'){
			switch (this.balance) {
			case RIGHT:
				if(this.right.balance == Code.RIGHT || this.right.balance == Code.SAME){
					this.rotateLeft();
					if(parentDirection){
						this.parent.parent.updateBalanceDelete('l', numRotations + 1);
					}
					else{
						this.parent.parent.updateBalanceDelete(bal, numRotations + 1);
					}
				}
				else if(this.right.balance == Code.LEFT){
					int bSlant = 0;//left is -1, right is 1
					if(this.right.left.balance == Code.LEFT){
						bSlant = -1;
					}
					else if(this.right.left.balance == Code.RIGHT){
						bSlant = 1;
					}
					else{
						bSlant = 0;
					}
					this.right.rotateRight();
					this.rotateLeft();
					if(bSlant == 1){
						this.balance = Code.LEFT;//TODO think it is here
					}
					else if(bSlant == -1){
						this.parent.right.balance = Code.RIGHT;
					}
					if(parentDirection){
						this.parent.parent.updateBalanceDelete('l', numRotations + 2);
					}
					else{
						this.parent.parent.updateBalanceDelete(bal, numRotations + 2);
					}
				}
				
				break;
			case LEFT:
				this.balance = Code.SAME;
				if(parentDirection){
					this.parent.updateBalanceDelete('l', numRotations);
				}
				else{
					this.parent.updateBalanceDelete(bal, numRotations);
				}
				break;
			case SAME:
				this.balance = Code.RIGHT;
				break;
			}
		}
		else if(bal == 'l'){
			switch (this.balance) {
			case RIGHT:
				this.balance = Code.SAME;
				if(parentDirection){
					this.parent.updateBalanceDelete(bal, numRotations);
				}
				else{
					this.parent.updateBalanceDelete('r', numRotations);
				}
				break;
			case LEFT:
				if(this.left.balance == Code.LEFT || this.left.balance == Code.SAME){
					this.rotateRight();
					if(parentDirection){
						this.parent.parent.updateBalanceDelete(bal, numRotations + 1);
					}
					else{
						this.parent.parent.updateBalanceDelete('r', numRotations + 1);
					}
				}
				else if(this.left.balance == Code.RIGHT){
					int bSlant = 0;//left is -1, right is 1
					if(this.left.right.balance == Code.LEFT){
						bSlant =-1;
					}
					else if(this.left.right.balance == Code.RIGHT){
						bSlant = 1;
					}
					else{
						bSlant = 0;
					}
					this.left.rotateLeft();
					this.rotateRight();
					if(bSlant == -1){
						this.balance = Code.RIGHT;
					}
					else if(bSlant == 1){
						this.parent.left.balance = Code.LEFT;
					}
					if(parentDirection){
						this.parent.parent.updateBalanceDelete(bal, numRotations + 2);
					}
					else{
						this.parent.parent.updateBalanceDelete('r', numRotations + 2);
					}
				}
				break;
			case SAME:
				this.balance = Code.LEFT;
				break;
			}
		}
	}
	
	
	
	
	//Code for Milestone 2 constructor

	public void fillCoder(StringHolder chars, StringHolder children, ArrayList<Integer> ranks, StringHolder balanceCodes) {
		if(this == EditTree.NULL_NODE){
			return;
		}
		chars.setData(chars.getData() + this.element);
		ranks.add(this.rank);
		switch(this.balance){
			case LEFT:
				balanceCodes.setData(balanceCodes.getData() + "/");
				break;
			case RIGHT:
				balanceCodes.setData(balanceCodes.getData() + "\\");
				break;
			case SAME:
				balanceCodes.setData(balanceCodes.getData() + "=");
				break;
		}
		if(this.numChildren() == 1){
			children.setData(children.getData() + "R");
		}
		else if(this.numChildren() == -1){
			children.setData(children.getData() + "L");
		}
		else if(this.numChildren() == 0){
			children.setData(children.getData() + "0");
		}
		else{
			children.setData(children.getData() + "2");
		}
		this.left.fillCoder(chars, children, ranks, balanceCodes);
		this.right.fillCoder(chars, children, ranks, balanceCodes);
	}

	public void fillTree(StringHolder chars, StringHolder children, ArrayList<Integer> ranks, StringHolder balanceCodes) {
		if(chars.getData().length() == 0){
			return;
		}
		Code currBalance = null;
		switch(balanceCodes.getData().charAt(0)){
			case '\\':
				currBalance = Code.RIGHT;
				break;
			case '/':
				currBalance = Code.LEFT;
				break;
			case '=':
				currBalance = Code.SAME;
				break;
		}
		if(children.getData().charAt(0) == '2'){		
			this.left = new Node(chars.getData().charAt(0), this.treeForRootUpdate, ranks.get(0), currBalance, this);
			children.setData(children.getData().substring(1));	
			chars.setData(chars.getData().substring(1));
			ranks.remove(0);
			balanceCodes.setData(balanceCodes.getData().substring(1));
			this.left.fillTree(chars, children, ranks, balanceCodes);
			switch(balanceCodes.getData().charAt(0)){
				case '\\':
					currBalance = Code.RIGHT;
					break;
				case '/':
					currBalance = Code.LEFT;
					break;
				case '=':
					currBalance = Code.SAME;
					break;
			}
			this.right = new Node(chars.getData().charAt(0), this.treeForRootUpdate, ranks.get(0), currBalance, this);
			chars.setData(chars.getData().substring(1));
			ranks.remove(0);
			balanceCodes.setData(balanceCodes.getData().substring(1));
			this.right.fillTree(chars, children, ranks, balanceCodes);
		}
		else if(children.getData().charAt(0) == '0'){
			children.setData(children.getData().substring(1));
			return;
		}
		else if(children.getData().charAt(0) == 'L'){
			this.left = new Node(chars.getData().charAt(0), this.treeForRootUpdate, ranks.get(0), currBalance, this);
			children.setData(children.getData().substring(1));
			chars.setData(chars.getData().substring(1));
			ranks.remove(0);
			balanceCodes.setData(balanceCodes.getData().substring(1));
			this.left.fillTree(chars, children, ranks, balanceCodes);		
		}
		else{
			this.right = new Node(chars.getData().charAt(0), this.treeForRootUpdate, ranks.get(0), currBalance, this);
			children.setData(children.getData().substring(1));		
			chars.setData(chars.getData().substring(1));
			ranks.remove(0);
			balanceCodes.setData(balanceCodes.getData().substring(1));
			this.right.fillTree(chars, children, ranks, balanceCodes);
		}
	
	}
	//TODO here on down is not done
	public String treeGetHelp(int length, String current) {
		if (length<=0) {
			return current;
		}
		boolean parentDirection;
		if (this.parent.left == this) {
			parentDirection = true;
		}
		else{
			parentDirection = false;
		}
		if (parentDirection) {
			return this.parent.treeGetHelp(length-1, current + this.element);
		}
		else if (this.left != EditTree.NULL_NODE) {
			return this.left.treeGetHelp(length-1, current + this.element);
		}
		return current;
	}
	public Node findNode(int pos){
		if(this.rank == pos){
			return this;
		}
		else if(this.rank > pos){
			return this.left.findNode(pos);
		}
		else{
			return this.right.findNode(pos - (this.rank + 1));
		}
	}
	public int find(String s, int pos, int treeSize){
		if (pos + s.length() >= treeSize) {
			
		}
		return pos;
	}
	
	
	//Working on concatenate Here. Still need to check these methods.

	public boolean insertAtHeightRight(Node farRight, int otherHeight, int thisHeight) {
		// TODO Auto-generated method stub.
		if(thisHeight == otherHeight){
			//TODO stuff
			this.parent.right = farRight;
			farRight.parent = this.parent;
			this.parent = farRight;
			farRight.left = this;
			farRight.rank = this.size();
//			if(farRight.left.height() == farRight.right.height() + 1){
//				farRight.balance = Code.LEFT;
//			}
//			else if(farRight.left.height() == farRight.right.height() - 1){
//				farRight.balance = Code.RIGHT;
//			}
//			else if(farRight.left.height() == farRight.right.height()){
//				farRight.balance = Code.SAME;
//			}
//			else{
//				System.out.println("this should never happen");
//			}
			farRight.parent.updateBalance('r');
			return true;
		}
		else if(thisHeight == otherHeight + 1 && this.balance == Code.LEFT){
			this.parent.right = farRight;
			farRight.parent = this.parent;
			this.parent = farRight;
			farRight.left = this;
			farRight.rank = this.size();
			farRight.balance = Code.LEFT;
			farRight.parent.updateBalance('r');
			return true;
		}
		else if(this.right == EditTree.NULL_NODE){
			//TODO, lots of update balance stuff
			this.right = farRight;
			farRight.parent = this;
			if(this.left.height() == this.right.height() + 1){
				this.updateBalance('l');
			}
			else if(this.left.height() == this.right.height() - 1){
				this.updateBalance('r');
			}
			else if(this.left.height() == this.right.height()){
			}
			else{
				if(this.left.height() > this.right.height()){
					this.rotateRight();
				}
				else{
					this.rotateLeft();
				}
			}
			return false;
		}
		else{
			if(this.balance == Code.LEFT){
				thisHeight -= 2;
			}
			else{
				thisHeight --;
			}
			return this.right.insertAtHeightRight(farRight, otherHeight, thisHeight);
		}
	}

	public boolean insertAtHeightLeft(Node farLeft, int thisHeight, int otherHeight) {
		// TODO Auto-generated method stub.
		if(otherHeight == thisHeight){
			//TODO stuff
			this.parent.left = farLeft;
			farLeft.parent = this.parent;
			this.parent = farLeft;
			farLeft.right = this;
			int numRotations = farLeft.left.numRotations;
			Node temp = farLeft;
			while(temp.parent != EditTree.NULL_NODE){
				temp = temp.parent;
			}
			temp.numRotations = numRotations;
			farLeft.left.numRotations = 0;
//			if(farLeft.left.height() == farLeft.right.height() + 1){
//				farLeft.balance = Code.LEFT;
//			}
//			else if(farLeft.left.height() == farLeft.right.height() - 1){
//				farLeft.balance = Code.RIGHT;
//			}
//			else if(farLeft.left.height() == farLeft.right.height()){
//				farLeft.balance = Code.SAME;
//			}
//			else{
//				System.out.println("this should never happen");
//			}
			farLeft.parent.updateBalance('l');
			return true;
		}
		else if(otherHeight == thisHeight + 1 && this.balance == Code.RIGHT){
			this.parent.left = farLeft;
			farLeft.parent = this.parent;
			this.parent = farLeft;
			farLeft.right = this;
			farLeft.balance = Code.RIGHT;
			int numRotations = farLeft.left.numRotations;
			Node temp = farLeft;
			while(temp.parent != EditTree.NULL_NODE){
				temp = temp.parent;
			}
			temp.numRotations = numRotations;
			farLeft.left.numRotations = 0;
			farLeft.parent.updateBalance('l');
			return true;
		}
		else if(this.left == EditTree.NULL_NODE){
			//TODO lots of update balance stuff
			this.left = farLeft;
			farLeft.parent = this;
			this.rank = farLeft.size();
			if(this.left.height() == this.right.height() + 1){
				this.updateBalance('l');
			}
			else if(this.left.height() == this.right.height() - 1){
				this.updateBalance('r');
			}
			else if(this.left.height() == this.right.height()){
				
			}
			else{
				if(this.left.height() > this.right.height()){
					this.rotateRight();
				}
				else{
					this.rotateLeft();
				}
			}
			int numRotations = farLeft.left.numRotations;
			Node temp = farLeft;
			while(temp.parent != EditTree.NULL_NODE){
				temp = temp.parent;
			}
			temp.numRotations = numRotations;
			farLeft.left.numRotations = 0;
			return false;
		}
		else{
			this.rank += farLeft.size();
			if(this.balance == Code.RIGHT){
				otherHeight -= 2;
			}
			else{
				otherHeight --;
			}
			return this.left.insertAtHeightLeft(farLeft, thisHeight, otherHeight);
		}
	}
	
	
}