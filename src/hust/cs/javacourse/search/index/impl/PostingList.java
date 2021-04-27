package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * AbstractPosting是Posting对象的抽象父类.
 *      Posting对象代表倒排索引里每一项， 一个Posting对象包括:
 *          包含单词的文档id.
 *          单词在文档里出现的次数.
 *          单词在文档里出现的位置列表（位置下标不是以字符为编号，而是以单词为单位进行编号.
 *      必须实现下面二个接口:
 *          Comparable：可比较大小（按照docId大小排序）,
 *                      当检索词为二个单词时，需要求这二个单词对应的PostingList的交集,
 *                      如果每个PostingList按docId从小到大排序，可以提高求交集的效率.
 *          FileSerializable：可序列化到文件或从文件反序列化
 *  </pre>
 */
public class PostingList extends AbstractPostingList {
    /**
     * 添加Posting,要求不能有内容重复的posting
     * @param posting：Posting对象
     */
    @Override
    public void add(AbstractPosting posting){
        if(posting.getFreq()==0)return;
        for(AbstractPosting p:this.list){
            if(p.getDocId()==posting.getDocId()){
                for(int po:posting.getPositions()) {
                    if(!p.getPositions().contains(po)) {
                        p.getPositions().add(po);
                        p.setFreq(p.getFreq() + 1);
                    }
                }
                return;
            }
        }
        this.list.add(posting);
    }

    @Override
    /**
     * 添加Posting列表,,要求不能有内容重复的posting
     * @param postings：Posting列表
     */
    public void add(List<AbstractPosting> postings){
        for(AbstractPosting posting:postings){
            this.add(posting);
        }
    }

    /**
     * 获得PosingList的字符串表示
     * @return ： PosingList的字符串表示
     */
    @Override
    public String toString() {
        return this.list.toString();
    }



    /**
     * 返回指定下标位置的Posting
     * @param index ：下标
     * @return： 指定下标位置的Posting
     */
    @Override
    public AbstractPosting get(int index){
        return this.list.get(index);
    }

    /**
     * 返回指定Posting对象的下标
     * @param posting：指定的Posting对象
     * @return ：如果找到返回对应下标；否则返回-1
     */
    @Override
    public int indexOf(AbstractPosting posting){
        return this.list.indexOf(posting);
    }

    /**
     * 返回指定文档id的Posting对象的下标
     * @param docId ：文档id
     * @return ：如果找到返回对应下标；否则返回-1
     */
    @Override
    public int indexOf(int docId){
        for(AbstractPosting i: list)
        {
            if(i.getDocId()==docId){
                return this.list.indexOf(i);
        }
        }
        return -1;
    }

    /**
     * 是否包含指定Posting对象
     * @param posting： 指定的Posting对象
     * @return : 如果包含返回true，否则返回false
     */
    @Override
    public boolean contains(AbstractPosting posting){
        return this.list.contains(posting);
    }

    /**
     * 删除指定下标的Posting对象
     * @param index：指定的下标
     */
    @Override
    public void remove(int index){
        this.list.remove(index);
    }

    /**
     * 删除指定的Posting对象
     * @param posting ：定的Posting对象
     */
    @Override
    public void remove(AbstractPosting posting){
        this.list.remove(posting);
    }

    /**
     * 返回PostingList的大小，即包含的Posting的个数
     * @return ：PostingList的大小
     */
    @Override
    public int size(){
        return this.list.size();
    }

    /**
     * 清除PostingList
     */
    @Override
    public void clear(){
        this.list.clear();
    }

    /**
     * PostingList是否为空
     * @return 为空返回true;否则返回false
     */
    @Override
    public boolean isEmpty(){
        return this.list.isEmpty();
    }

    /**
     * 根据文档id的大小对PostingList进行从小到大的排序
     */
    @Override
    public  void sort(){
        Collections.sort(this.list);
    }


    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out){
        try {
            out.writeObject(this.list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in){
        try {
            this.list = (List<AbstractPosting>)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
