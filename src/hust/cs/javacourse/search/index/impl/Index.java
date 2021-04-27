package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 缺省构造函数
     */
    public Index() {
    }

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        return this.termToPostingListMapping.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        //添加新的docId与docPath的映射关系
        this.docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());


        Map<AbstractTerm, List<Integer>> map = new HashMap<AbstractTerm, List<Integer>>();

        //遍历该文件中所有的三元组，document.getTuples()得到所有的三元组直到为null
        for(AbstractTermTuple termTuple : document.getTuples()){
            //此单词键值还没有添加到列表中
            if(map.get(termTuple.term) == null) {
                //添加此单词键值，并初始化位置列表
                map.put(termTuple.term, new ArrayList<Integer>());
            }
            // 找到对应单词，获得其对应的位置列表，并将所有单词的位置都添加到其中
            map.get(termTuple.term).add(termTuple.curPos);
        }

        // 更新倒排索引，遍历所有的单词键值
        for(AbstractTerm term : map.keySet()){

            //单词文档列表中若不存在此单词，则添加此单词
            if(this.termToPostingListMapping.get(term) == null) {
                //添加单词——列表对
                this.termToPostingListMapping.put(term, new PostingList());
            }

            //为此单词——列表添加一个新的Posting，包括此文档的Id。当前单词在文中出现的次数，以及出现的位置
            this.termToPostingListMapping.get(term).add(
                    new Posting(document.getDocId(), map.get(term).size(), map.get(term)));
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
            readObject(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            writeObject(output);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return this.termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return new HashSet<AbstractTerm>(this.termToPostingListMapping.keySet());
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for(AbstractPostingList list : this.termToPostingListMapping.values()) {
            list.sort();            // 对PostingList排序
            for(int i=0;i<list.size();i++){
                list.get(i).sort(); // 对position排序
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return this.docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.termToPostingListMapping);
            out.writeObject(this.docIdToDocPathMapping);
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
    public void readObject(ObjectInputStream in) {
        try {
            this.termToPostingListMapping = (Map<AbstractTerm, AbstractPostingList>)in.readObject();
            this.docIdToDocPathMapping = (Map<Integer, String>)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
