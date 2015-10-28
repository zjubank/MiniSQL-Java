package myminisql;

public class BplusTree {
	
	public static final int PointerLength = 4;
	private int Min;
	private int Max;
	private int MinIndex;
	private int MaxIndex;
	public String filename;
	private Index index;
	private Buffer Root; //根buffer块
	
	public BplusTree(Index index){
		/* 加入文件的建立？*/

		/*一个block能够放下多少个index*/
		MaxIndex=(int)Math.floor((4096.0-1/*叶子标记为是leaf还是internal node*/-4/*有多少个索引键*/-PointerLength/*父亲块号*/-PointerLength/*兄弟块的块号*/)/(8+index.Size));
		MinIndex=(int)Math.ceil(1.0 * MaxIndex/ 2);
		Max = MaxIndex; //一个block能放下多少index根据设定B+树的序数。
		Min = (int)Math.ceil(1.0 * Max/ 2);
	}
	
	public void insert(byte[] key, address a){
		TreeNode rootnode;
		if (Root.block[0] == 1) {
			rootnode = new InnerNode(Root,true);
		}
		else {
			rootnode = new LeafNode(Root,true);
		}
		
		byte[] attrkey = new byte[index.Size]; //要把key扩展为跟现在的key一样长
		
		for (int i = 0; i < key.length; i++) {
			attrkey[i] = key[i];
		}
		for (int i = key.length; i < index.Size; i++) {
			attrkey[i] = '*'; //没有值的用*填塞。
		}
		
		Buffer newRoot=rootnode.insert(attrkey, a); //节点的插入操作
	    
		if(newRoot!=null){ //假如有返回，说明根块被更新了
			Root=newRoot;
		}
		
	}
	
	public void delete(byte[] key) {
		TreeNode rootnode;
		if (Root.block[0] == 1) {
			rootnode = new InnerNode(Root,true);
		}
		else {
			rootnode = new LeafNode(Root,true);
		}
		
		byte[] attrkey = new byte[index.Size]; //要把key扩展为跟现在的key一样长
		
		for (int i = 0; i < key.length; i++) {
			attrkey[i] = key[i];
		}
		for (int i = key.length; i < index.Size; i++) {
			attrkey[i] = '*'; //没有值的用*填塞。
		}
		
		Buffer newRoot=rootnode.delete(attrkey); //节点的插入操作
	    
		if(newRoot!=null){ //假如有返回，说明根块被更新了
			Root=newRoot;
		}
	}
	
	public address search(byte[] key) {
		TreeNode rootnode;
		if (Root.block[0] == 1) {
			rootnode = new InnerNode(Root, true);
		}
		else {
			rootnode = new LeafNode(Root,true);
		}
		
		byte[] attrkey = new byte[index.Size]; //要把key扩展为跟现在的key一样长
		
		for (int i = 0; i < key.length; i++) {
			attrkey[i] = key[i];
		}
		for (int i = key.length; i < index.Size; i++) {
			attrkey[i] = '*'; //没有值的用*填塞。
		}
		
		return rootnode.search(attrkey);
	}
	
	public abstract class TreeNode {
		Buffer nodeblock;
		abstract Buffer insert(byte[] key, address a);
		abstract Buffer delete(byte[] key);
		abstract address search(byte[] key);
		public int compare(byte[] buffer1,byte[] buffer2) {
			
			for (int i = 0, j = 0; i < buffer1.length && j < buffer2.length; i++, j++) {
				int a = (buffer1[i] & 0xff);
				int b = (buffer2[j] & 0xff);
				if (a != b) {
					return a - b;
				}
			}
			return buffer1.length - buffer2.length;
		}
	}
	
	public class InnerNode extends TreeNode{
		//内部节点的存储结构，应该是第一位用来标明是否是叶子节点，第2到5表明自己这个block是有多少个key，6到9，父亲节点的指针，然后是每个key的指针，和值。
		public InnerNode(Buffer block, boolean isexistnode) {
			nodeblock = block;
			if (!isexistnode) {
				nodeblock.block[0] = '0'; //不是叶子节点
				nodeblock.setInt(1,0);
				nodeblock.clear(5,4);
			}
		}
		@Override
		Buffer insert(byte[] key, address a) {
			// TODO Auto-generated method stub
			
			int keynum = nodeblock.getInt(1);
			int i = 0;
			for (i = 0; i < keynum; i++) {
				int start = 9 + PointerLength + i * (index.Size + PointerLength); //pointer | key | pointer这样的形式排列
				if (compare(key, nodeblock.getBytes(start, index.Size)) < 0) { //找到第一个key值比要插入的大的，插在它前面
					break;
				}
			}
			
			int childBlockNum = nodeblock.getInt(9 + i * (index.Size + PointerLength));//读进指针指向的子节点
			Buffer childblock = BufferManager.readBlock(filename, childBlockNum); //将这个子块读进来
			
			TreeNode child;
			if(childblock.block[0] == '0') 
				child = new InnerNode(childblock,true); //如果是内部节点 
			else child = new LeafNode(childblock,true); //如果是叶子节点
			
			return child.insert(key, a);
		}
		
		@Override
		Buffer delete(byte[] key) {
			// TODO Auto-generated method stub
			int keynum = nodeblock.getInt(1);
			int i = 0;
			for (i = 0; i < keynum; i++) {
				int start = 9 + PointerLength + i * (index.Size + PointerLength);
				if (compare(key,nodeblock.getBytes(start, index.Size)) < 0) {
					break;
				}
			}
			
			int childBlockNum = nodeblock.getInt(9 + i * (index.Size + PointerLength));//读进指针指向的子节点
			Buffer childblock = BufferManager.readBlock(filename, childBlockNum); //将这个子块读进来
			
			TreeNode child;
			if(childblock.block[0] == '0') 
				child = new InnerNode(childblock,true); //如果是内部节点 
			else child = new LeafNode(childblock,true); //如果是叶子节点
			
			return child.delete(key); 
		}

		@Override
		address search(byte[] key) {
			// TODO Auto-generated method stub
			int keynum = nodeblock.getInt(1);
			int i = 0;
			for (i = 0; i < keynum; i++) {
				int start = 9 + PointerLength + i * (index.Size + PointerLength);
				if (compare(key,nodeblock.getBytes(start, index.Size)) < 0) {
					break;
				}
			}
			
			int childBlockNum = nodeblock.getInt(9 + i * (index.Size + PointerLength));//读进指针指向的子节点
			Buffer childblock = BufferManager.readBlock(filename, childBlockNum); //将这个子块读进来
			
			TreeNode child;
			if(childblock.block[0] == '0') 
				child = new InnerNode(childblock,true); //如果是内部节点 
			else child = new LeafNode(childblock,true); //如果是叶子节点
			
			return child.search(key); 
		}
		
		Buffer split(byte[] key, TreeNode left, TreeNode right) {
			int keynum = nodeblock.getInt(1);
			
			if (keynum == 0) { //新节点
				keynum++;
				nodeblock.setInt(1,keynum);
				nodeblock.setBytes(9 + PointerLength, key); //第一个值设为自己的key
				nodeblock.setInt(9, left.nodeblock.blockOffset); //第一个指针指向左儿子
				nodeblock.setInt(9 + PointerLength + key.length, right.nodeblock.blockOffset); //第二个指针指向右儿子
				return this.nodeblock; //新的根
			}
			
			if (++keynum <= Max) { // 内部节点没有满
				int i;
				for (i = 0; i < keynum - 1; i++) {
					int start = 9 + PointerLength + i * (index.Size + PointerLength);
					if (compare(key,nodeblock.getBytes(start, index.Size)) < 0) { //遇见了第一个比插入的值大的key，在他前面插入
						//把要插入的位置留出来
						System.arraycopy(nodeblock.block, 9 + PointerLength + i * (index.Size + PointerLength), nodeblock.block, 9 + PointerLength + (i+1) * (index.Size + PointerLength), PointerLength + (keynum - 1 - i)*(index.Size + PointerLength));
						nodeblock.insertkey(9 + i * (index.Size + PointerLength), key, right.nodeblock.blockOffset);
						nodeblock.setInt(1, keynum); //更新键的数目
						return null;
					}
				}
				if (i == keynum - 1) {
					nodeblock.insertkey(9 + i *(index.Size + PointerLength), key, right.nodeblock.blockOffset);
					nodeblock.setInt(1, keynum);
					return null;
				}
			} 
			else {//内部节点满了
				Buffer slibingblock = BufferManager.create(filename, index.BlockNum);//建立一块新的buffer
				index.BlockNum ++;
				InnerNode slibing = new InnerNode(slibingblock,false); //分裂节点，为兄弟
				boolean Infirst = false; //要插入的键值是否处于前一半	
				nodeblock.setInt(1, Min);
				slibingblock.setInt(1, Max - Min + 1);
				
				for (int i = 0; i < Min; i++){//要插入的键值会处于前一半
					int start = 13 + i * (index.Size + PointerLength); // i号键的地址
					if (compare(key, nodeblock.getBytes(start, index.Size)) < 0) {
						//要先把后半部分的东西移到下一个block，然后再把这个block内的空位移出来
						System.arraycopy(nodeblock.block, 9 + Min*(index.Size + PointerLength), slibingblock.block,9, PointerLength + (Max - Min)*(index.Size + PointerLength));
						System.arraycopy(nodeblock.block, 9 + i*(index.Size + PointerLength), nodeblock.block, 9 + (i + 1)*(index.Size + PointerLength), PointerLength + (MinIndex - 1 - i)*(index.Size + PointerLength));
						nodeblock.insertkey(9 + PointerLength + i*(index.Size + 8) ,key, right.nodeblock.blockOffset);
						Infirst = true;
						break;
					}
				}
				
				if (!Infirst) {//要插入的键值会属于后一半
					System.arraycopy(nodeblock.block, 9 + (Min+1)*(index.Size + PointerLength), slibingblock, 9, PointerLength + (Max - Min - 1)*(index.Size + PointerLength));
					int i = 0;
					for (i = 0; i < Max - Min - 1; i++){
						int start = 9 + PointerLength + i * (index.Size + PointerLength);
						if (compare(key,slibingblock.getBytes(start, index.Size)) < 0) {
							System.arraycopy(slibingblock.block, 9 + i*(index.Size + PointerLength), slibingblock.block, 9 + (i + 1)*(index.Size + PointerLength), PointerLength+(MaxIndex-MinIndex-i)*(index.Size + PointerLength));
							slibingblock.insertkey(9 + i *(index.Size + PointerLength), key, right.nodeblock.blockOffset);
							break;
						}
					}
				}
				
				//get 正中间的key，将上传给父亲节点
				byte[] midkey = nodeblock.getBytes(9 + PointerLength + (Min)*(index.Size+PointerLength), index.Size);
				
				//更新新建的子块的父亲
				for (int j = 0; j <= slibingblock.getInt(1); j++){//遍历新的节点的所有的子key节点
					int childBlockNum=slibingblock.getInt(9 + j*(index.Size + PointerLength));
					BufferManager.readBlock(filename, childBlockNum).setInt(5, index.BlockNum - 1);//把它们的父节点设为新建的这个节点。					
				}	
	
				int parentBlockNum;
			    Buffer parentblock;
			    InnerNode parent;
				if(nodeblock.block[5] == '*'){  //没有父节点，需要创建父节点
					parentBlockNum=index.BlockNum; //父节点的块号
					parentblock=BufferManager.create(filename, parentBlockNum);//从内存中创建				
					index.BlockNum++;		
					nodeblock.setInt(5, parentBlockNum);//当前节点的父节点设置为新建父节点
					slibingblock.setInt(5, parentBlockNum);//兄弟节点的父节点设置为新建父节点
					parent=new InnerNode(parentblock,false);
				}
				else{
					parentBlockNum = nodeblock.getInt(5);				
					slibingblock.setInt(5, parentBlockNum); //新节点的父亲也就是旧节点的父亲
					parentblock = BufferManager.readBlock(filename, parentBlockNum);
					parent = new InnerNode(parentblock,true);
				}
				
				return parent.split(midkey, this, slibing);
			}
			return null;
			
		}
		
		Buffer combine(byte[] midkey, Buffer nextblock) {
			int keynum = nodeblock.getInt(1);
			int nextkeynum = nextblock.getInt(1);
			System.arraycopy(nextblock.block, 9, nodeblock, 9+(keynum+1)*(index.Size+PointerLength), PointerLength + nextkeynum*(index.Size + PointerLength));
			nodeblock.setBytes(9 + keynum*(index.Size+PointerLength) + PointerLength, midkey);
			keynum = keynum + nextkeynum + 1;
			nodeblock.setInt(1,keynum);
			int parentBlockNum = nodeblock.getInt(5);
			Buffer parentblock = BufferManager.readBlock(filename, parentBlockNum);
			index.BlockNum--;
			return (new InnerNode(parentblock,true).delete(nextblock));
		}

		public void exchange(byte[] key, int keyBlockNum) {
			int keynum = nodeblock.getInt(1);
			int i = 0;
			int childBlockNum;
			for(i= 0; i < keynum; i++) {
				int start = 9+ i*(index.Size+ PointerLength);
				childBlockNum = nodeblock.getInt(start);
				if (childBlockNum == keyBlockNum) { //找到了一个儿子跟要求修改的块号一样
					break;
				}
			}
			if (i < keynum) { //有需要修改的儿子块号
				nodeblock.setBytes(9 + i * (index.Size + PointerLength) + PointerLength, key);//把这个块号的值变为key
			}
		}
		
		byte[] allocate(Buffer siblingblock, byte[] key, String relation) {
			int siblingkeynum = siblingblock.getInt(1);
			int keynum = nodeblock.getInt(1);
			
			if (relation.equals("After")) {
				int siblingBlockNum = siblingblock.getInt(9);
				//兄弟的第一个儿子的块号
				nodeblock.insertkey(9 + PointerLength + keynum *(index.Size + PointerLength), key, siblingBlockNum);
				//把兄弟的第一个儿子插入自己的尾部。
				siblingkeynum--;
				siblingblock.setInt(1,siblingkeynum);
				byte[] midkey = siblingblock.getBytes(9 + PointerLength, index.Size); //兄弟的第一个儿子key。即分离的midkey
				System.arraycopy(siblingblock.block, 9 +PointerLength+index.Size, siblingblock.block, 9, siblingkeynum*(PointerLength+index.Size));
				return midkey;
			}
			else {
				siblingkeynum--;
				siblingblock.setInt(1,siblingkeynum);
				byte[] midkey = siblingblock.getBytes(9 + PointerLength + siblingkeynum*(PointerLength + index.Size),index.Size);
				//兄弟的最后一个儿子把兄弟和自己分离开来
				int siblingBlockNum = siblingblock.getInt(9 + siblingkeynum*(index.Size + PointerLength));
				System.arraycopy(nodeblock.block, 9 , nodeblock.block, 9 + PointerLength+index.Size, PointerLength + keynum*(PointerLength + index.Size));
				nodeblock.setInt(9, siblingBlockNum);
				nodeblock.setBytes(9 + PointerLength, key);
				keynum++;
				nodeblock.setInt(1,keynum);
				return midkey;
			}
			
		}
		Buffer delete(Buffer deleteblock){
			int keynum = nodeblock.getInt(1);
			for (int i = 0; i <= keynum; i++) {
				int start = 9 + i*(index.Size + PointerLength);
				int pointer = nodeblock.getInt(start);
				if (pointer == deleteblock.blockOffset) { //找到了要删除的块号
					System.arraycopy(nodeblock.block, 9 + PointerLength + (i-1)*(index.Size + PointerLength), nodeblock.block, 9 + PointerLength + i*(index.Size + PointerLength), (keynum-i)*(index.Size + PointerLength));
					keynum--;
					nodeblock.setInt(1,keynum);
					if (keynum >= Min) { //如果删除不会造成任何影响，则删除后直接结束
						return null;
					}
					
					if (nodeblock.block[5] == '*') {//删除后，节点小于最小值，且没有父亲
						if (keynum == 0) {//只有一个子块，没有key作为分界值
							index.BlockNum--;
							return BufferManager.readBlock(filename, nodeblock.getInt(9));//子块作为根
						}
						return null;
					}
					
					int parentBlockNum = nodeblock.getInt(5); //找到父亲节点
					Buffer parentblock = BufferManager.readBlock(filename, parentBlockNum);
					int parentkeynum = parentblock.getInt(1);
					int siblingBlockNum;
					Buffer siblingblock;
					
					int j = 0;
					for (j = 0; j < parentkeynum; j++) {
						int sstart = 9 + j*(index.Size+PointerLength);
						if (nodeblock.blockOffset == parentblock.getInt(sstart)) {
							siblingBlockNum = parentblock.getInt(sstart + PointerLength + index.Size);
							siblingblock = BufferManager.readBlock(filename, siblingBlockNum);
							//找到后面的兄弟块
							byte[] midkey = parentblock.getBytes(sstart + PointerLength, index.Size);
							if ((siblingblock.getInt(1) + keynum) <= Max) {
								return this.combine(midkey,siblingblock);
							}
							(new InnerNode(parentblock,true)).exchange(allocate(siblingblock,midkey,"After"),nodeblock.blockOffset);
							//父亲节点的两个儿子由于进行了重新分配，因此midkey也变了，所以指针要相应改变。
							return null;
						}
					}
					//已经是最后一个节点，只能找有没有前面的兄弟节点
					siblingBlockNum = parentblock.getInt(9+(parentkeynum-1)*(index.Size + PointerLength));
					siblingblock = BufferManager.readBlock(filename, siblingBlockNum);
					//找到前面的兄弟节点
					byte[] midkey = parentblock.getBytes(9 + (parentkeynum-1)*(index.Size + PointerLength)+PointerLength,index.Size);
					
					if ((siblingblock.getInt(1) + keynum) <= Max) { //如果可以合并
						return (new InnerNode(siblingblock,true)).combine(midkey, nodeblock);
					}
					(new InnerNode(parentblock,true)).exchange(allocate(siblingblock,midkey,"Before"),siblingBlockNum);
					return null;
				}
			}
			return null;
		}
	}
	
	public class LeafNode extends TreeNode{
		
		public LeafNode(Buffer creatblock, boolean isexistnode){
			nodeblock = creatblock;
			if (!isexistnode) {   //不是已经存在的节点，那么是新建的，要把头文件信息初始化。
				nodeblock.block[0] = 1; //是叶子节点
				nodeblock.setInt(1,0); //总共的键值数为0
				nodeblock.clear(5,8); 
			}
		}
		
		@Override
		Buffer insert(byte[] key, address a) {
			// TODO Auto-generated method stub
			int keynum = nodeblock.getInt(1);
			if (++keynum <= MaxIndex) { // 叶子节点没有满
				if (keynum - 1 == 0) {//这是一个新的叶子节点，那么要把指向自己父亲的pointer移到后面去，给自己腾出位置。
					System.arraycopy(nodeblock.block, 9, nodeblock.block, 9 + (index.Size + 8), PointerLength);
					nodeblock.insertkey(9,key,a);
					nodeblock.setInt(1, keynum);
					return null; //
				}
				int i;
				for (i = 0; i < keynum; i++) {
					int start = 17 + i * (index.Size + 8);
					
					if (compare(key,nodeblock.getBytes(start, index.Size)) == 0) { //匹配到已经存在的key，说明是更新value
						nodeblock.insertkey(9 + i * (index.Size + 8), key, a);
						return null;
					}
					
					if (compare(key,nodeblock.getBytes(start, index.Size)) < 0) { //遇见了第一个比插入的值大的key，在他前面插入
						//把要插入的位置留出来
						System.arraycopy(nodeblock.block, 9 + i * (index.Size + 8), nodeblock.block, 9 + (i+1) * (index.Size + 8), PointerLength + (keynum - 1 - i)*(index.Size + 8));
						nodeblock.insertkey(9 + i * (index.Size + 8), key, a);
						nodeblock.setInt(1, keynum); //更新键的数目
						return null;
					}
				}
				if (i == keynum) {
					System.arraycopy(nodeblock.block, 9 + (i - 1)*(index.Size + 8), nodeblock.block, 9 + i * (index.Size + 8), PointerLength);
					nodeblock.insertkey(9 + (i - 1)*(index.Size + 8), key, a);
					nodeblock.setInt(1, keynum);
					return null;
				}
			} 
			else {//叶子节点满了
				Buffer slibingblock = BufferManager.create(filename, index.BlockNum);//建立一块新的buffer
				index.BlockNum ++;
				LeafNode slibing = new LeafNode(slibingblock,false);
				boolean Infirst = false;
				for (int i = 0; i < MinIndex - 1; i++){//要插入的键值会处于前一半
					int start = 17 + i * (index.Size + 8); // i号键的地址
					if (compare(key, nodeblock.getBytes(start, index.Size)) < 0) {
						//要先把后半部分的东西移到下一个block，然后再把这个block内的空位移出来
						System.arraycopy(nodeblock.block, 9 + (MinIndex - 1)*(index.Size + 8), slibingblock.block,9, PointerLength + (MaxIndex - MinIndex + 1)*(index.Size + 8));
						System.arraycopy(nodeblock.block, 9 + i*(index.Size + 8), nodeblock.block, 9 + (i + 1)*(index.Size + 8), PointerLength + (MinIndex - 1 - i)*(index.Size + 8));
						nodeblock.insertkey(9 + i*(index.Size + 8) ,key, a);
						Infirst = true;
						break;
					}
				}
				
				if (!Infirst) {//要插入的键值会属于后一半
					System.arraycopy(nodeblock.block, 9 + MinIndex*(index.Size + 8), slibingblock, 9, PointerLength + (MaxIndex - MinIndex)*(index.Size + 8));
					int i = 0;
					for (i = 0; i < MaxIndex - MinIndex; i++){
						int start = 17 + i * (index.Size + 8);
						if (compare(key,slibingblock.getBytes(start, index.Size)) < 0) {
							System.arraycopy(slibingblock.block, 9 + i*(index.Size + 8), slibingblock.block, 9 + (i + 1)*(index.Size + 8), PointerLength+(MaxIndex-MinIndex-i)*(index.Size + 8));
							slibingblock.insertkey(9 + i *(index.Size + 8), key, a);
							break;
						}
					}
					
					if (i == MaxIndex - MinIndex) {
						System.arraycopy(slibingblock.block, 9 + i*(index.Size + 8), slibingblock.block, 9 + (i + 1)*(index.Size + 8), PointerLength+(MaxIndex-MinIndex-i)*(index.Size + 8));
						slibingblock.insertkey(9 + i *(index.Size + 8), key, a);
					}
				}
				
				nodeblock.setInt(1,MinIndex);
				slibingblock.setInt(1,MaxIndex-MinIndex);
				
				nodeblock.setInt(9 + MinIndex * (index.Size + 8), slibingblock.blockOffset);
				
				int parentBlockNum;
			    Buffer parentblock;
			    InnerNode parent;
				if(nodeblock.block[5] == '*'){  //没有父节点，需要创建父节点
					parentBlockNum=index.BlockNum;
					parentblock=BufferManager.create(filename, parentBlockNum);				
					index.BlockNum++;		
					nodeblock.setInt(5, parentBlockNum);
					slibingblock.setInt(5, parentBlockNum);
					parent=new InnerNode(parentblock,false);
				}
				else{
					parentBlockNum=nodeblock.getInt(5);				
					slibingblock.setInt(5, parentBlockNum); //新节点的父亲也就是旧节点的父亲
					parentblock=BufferManager.readBlock(filename, parentBlockNum);
					parent=new InnerNode(parentblock,true);
				}
			
				//让父块分裂出它们两个块
				byte[] midkey = slibingblock.getBytes(17, index.Size);				
				return  parent.split(midkey, this, slibing); //把自己，兄弟和分离的键提交给父亲
			}
			return null;
		}

		@Override
		Buffer delete(byte[] key) {
			// TODO Auto-generated method stub
			int keynum = nodeblock.getInt(1);
			for (int i = 0; i < keynum; i++) {
				int start = 17 + i * (index.Size + 8); // 一个叶子block的排列是先标识符，再4位表示keynum，再4位表示父亲节点的块号，再开始 address | key值，这样排列，最后是4位的兄弟块号
				
				if (compare(key,nodeblock.getBytes(start, index.Size)) < 0) {
					System.out.println("this entry not exist!");
					return null;
				}
				
				if (compare(key,nodeblock.getBytes(start, index.Size)) == 0) {//找到要删除的键
					System.arraycopy(nodeblock.block, 9 + (i+1) * (index.Size + 8), nodeblock.block, 9 + i * (index.Size + 8), PointerLength+(keynum-1-i)*(index.Size+8));
					keynum--;
					nodeblock.setInt(1,keynum);
					if (keynum >= Min) {
						return null;
					}
					if (nodeblock.block[5] == '*') { //是根
						return null;
					}
					
					boolean last = false;
					if (nodeblock.block[9 + keynum * (index.Size + 8)] == '*') {
						last = true; //没有下一块，是叶子节点最后一块
					}
					
					int siblingBlockNum = nodeblock.getInt(9 + keynum *(index.Size + 8));
					Buffer siblingblock = BufferManager.readBlock(filename, siblingBlockNum);
					int parentBlockNum = nodeblock.getInt(5);
					
					if (last || siblingblock == null || siblingblock.getInt(5) != parentBlockNum) { //是最后一块，或者没有兄弟，或者下一块不是自己的亲兄弟
						Buffer parentblock=BufferManager.readBlock(filename, parentBlockNum);
						int j=0;
						int parentkeynum=parentblock.getInt(1);
						for(j = 0; j < parentkeynum; j++){
							int sstart = 9 + PointerLength + j*(index.Size+PointerLength);
							if(compare(key,parentblock.getBytes(sstart, index.Size)) < 0){ //找到自己的兄弟节点
								siblingBlockNum=parentblock.getInt(sstart - 2 * PointerLength - index.Size);
								siblingblock=BufferManager.readBlock(filename, siblingBlockNum);
								break;
							}
						}
						
						if ((siblingblock.getInt(1) + keynum) <= MaxIndex) { //合并两个节点
							return (new LeafNode(siblingblock,true).combine(nodeblock));
						}
						else { //重新分配兄弟节点和它的子节点个数
							(new InnerNode(parentblock,true)).exchange(allocate(siblingblock,"Before"),siblingBlockNum);
							return null; 
						}
						
						
					}
					else { //有在他后面的兄弟节点
						if ((siblingblock.getInt(1) + keynum) <= MaxIndex) { //合并两个节点
							return this.combine(nodeblock);
						}
						else { //重新分配兄弟节点和它的子节点个数
							Buffer parentblock = BufferManager.readBlock(filename, parentBlockNum);
							(new InnerNode(parentblock,true)).exchange(allocate(siblingblock,"After"),siblingBlockNum);
							return null; 
						}
					}
				}
			}
			return null;
		}

		@Override
		address search(byte[] key) {
			// TODO Auto-generated method stub
			int keynum = nodeblock.getInt(1);
			if (keynum == 0) {
				return null;
			}
			byte[] thiskey = new byte[index.Size];
			
			for (int i = 0; i < key.length; i++) {
				thiskey[i] = key[i];
			}
			for (int i = key.length; i < index.Size; i++) {
				key[i] = '&';
			}
			
			boolean find = false;
			int start = 17;
			for (int i = 0; i < keynum; i++) {
				start = 17 + i * (index.Size + 8);
				if (compare(key,nodeblock.getBytes(start, index.Size)) == 0){  
					find = true;
                    break;  
                }  
			}
			
			if (find) {
				address a = new address();
				a.blockOffset = nodeblock.getInt(start - 8);
				a. offset = nodeblock.getInt(start - 4);
				return a;
			}
			else return null;
		}
		
		Buffer combine(Buffer nextblock) {
			int keynum = nodeblock.getInt(1);
			int nextkeynum = nextblock.getInt(1);
			System.arraycopy(nextblock.block, 9, nodeblock.block, 9 + keynum*(index.Size + 8), PointerLength+nextkeynum*(index.Size + 8));
			keynum += nextkeynum;
			nodeblock.setInt(1,keynum);
			index.BlockNum--;
			int parentBlockNum = nodeblock.getInt(5);
			Buffer parentblock = BufferManager.readBlock(filename, parentBlockNum);
			return (new InnerNode(parentblock,true)).delete(nextblock);
		}
		
		byte[] allocate(Buffer siblingblock, String relation){
			int siblingkeynum = siblingblock.getInt(1);
			int keynum = nodeblock.getInt(1);
			
			if (relation.equals("After")) { //兄弟节点在后面
				address a = new address();
				a.blockOffset = siblingblock.getInt(9);
				a.offset = siblingblock.getInt(13);
				//兄弟节点的地址信息
				byte key[] = siblingblock.getBytes(17, index.Size); //拿到要移过来的兄弟节点的第一个儿子
				siblingkeynum--;
				siblingblock.setInt(1,siblingkeynum);
				System.arraycopy(siblingblock.block, 9+8+index.Size, siblingblock.block, 9, PointerLength+siblingkeynum*(8+index.Size));
				byte[] midkey = siblingblock.getBytes(17,index.Size);
				nodeblock.insertkey(9+keynum*(index.Size + 8), key, a); //把兄弟节点的键值插入自己尾部。
				keynum++;
				nodeblock.setInt(1,keynum);
				nodeblock.setInt(9 + keynum*(index.Size + 8), siblingblock.blockOffset); //把自己的兄弟指针更新为兄弟节点
				return midkey;
			}
			else {
				siblingkeynum--;
				siblingblock.setInt(1,siblingkeynum);
				address a = new address();
				a.blockOffset = siblingblock.getInt(9 + siblingkeynum*(index.Size + 8));
				a.offset = siblingblock.getInt(13 + siblingkeynum *(index.Size + 8));
				byte[] key = siblingblock.getBytes(17 + siblingkeynum *(index.Size + 8), index.Size);
				siblingblock.setInt(9 + siblingkeynum*(index.Size + 8),nodeblock.blockOffset);//把兄弟节点的兄弟指针更新为自己
				System.arraycopy(nodeblock.block,9,nodeblock.block, 9+8+index.Size, PointerLength + keynum*(index.Size + 8));
				nodeblock.insertkey(9, key, a);
				keynum++;
				nodeblock.setInt(1,keynum);
				byte[] midkey = nodeblock.getBytes(17, index.Size);
				return midkey;

			}
		}
	}
	
}
