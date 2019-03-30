package main.fs;

import main.io.LogicalBlock;
import main.util.Util;

import java.util.Arrays;

public class BitMap {

    private boolean[] occupied;
    private int[] bitMap;
    private int[] mask;
    private  int[] mask2;

    public BitMap(int numberOfBlocks) {
        int intsNeeded = numberOfBlocks / 32;
        if(numberOfBlocks%32>0) intsNeeded++;
        this.bitMap=new int[intsNeeded];
        this.occupied = new boolean[numberOfBlocks];
        mask=new  int[32];
        mask2=new int[32];
        mask[31]=1;
        mask2[31]=~mask[31];
        for(int i=30;i>=0;i--){
            mask[i]=mask[i+1]<<1;
            mask2[i]=~mask[i];

        }
    }


    public boolean isFreeBlock(int index){
        return ((bitMap[index/32]&mask[index%32])==0);

    }



    public static BitMap fromBlock(int numberOfBlocks, LogicalBlock block) {
        BitMap bitMap = new BitMap(numberOfBlocks);

        for (int i = 0; i < numberOfBlocks/32+1; i++) {
            bitMap.bitMap[i]=block.getInt(i);
        }

        System.out.println(Arrays.toString(bitMap.bitMap));
        return bitMap;
    }

    public void setOccupied(int index){
        bitMap[index/32]=bitMap[index/32]|mask[index%32];
    }

   public void setFree(int index) {
       bitMap[index/32]=bitMap[index/32]&mask2[index%32];
   }


    public LogicalBlock asBlock() {
        LogicalBlock block = new LogicalBlock();
        for(int i=0;i<bitMap.length;i++){
                block.setInt(i,bitMap[i]);
        }
        return block;
    }

    @Override
    public String toString() {
        String result="BitMap{occupied=";
        for(int i=0;i<bitMap.length;i++){
            result+=String.format("%32s",Integer.toBinaryString(bitMap[i])).replace(' ','0');
        }
        result+='}';
        return result;
    }
}
