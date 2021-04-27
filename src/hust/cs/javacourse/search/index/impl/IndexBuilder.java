package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <pre>
 * AbstractIndexBuilder是索引构造器的抽象父类
 *      需要实例化一个具体子类对象完成索引构造的工作
 * </pre>
 */
public class IndexBuilder extends AbstractIndexBuilder{

    public IndexBuilder(AbstractDocumentBuilder docBuilder){
        super(docBuilder);
        this.docId=0;
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory){
        AbstractIndex index = new Index();
        //获取所有的文件
        List<String> filePaths = FileUtil.list(rootDirectory);
        for(String path: filePaths){
            //构建Document
            AbstractDocument document = this.docBuilder.build(docId++,path,new File(path));

            //为index添加Document，由于docId是递增的，因此直接在Index中的term-PostingList键值对中直接添加Posting就可以
            index.addDocument(document);
        }


        return index;
    }

}
