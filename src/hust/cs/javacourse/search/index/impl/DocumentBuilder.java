package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;

import hust.cs.javacourse.search.parse.impl.*;

import java.io.*;

/**
 * <pre>
 * AbstractDocumentBuilder是Document构造器的抽象父类.
 *      Document构造器的功能应该是由解析文本文档得到的TermTupleStream，产生Document对象.
 * </pre>
 */
public class DocumentBuilder extends AbstractDocumentBuilder {


    /**
     * <pre>
     * 由给定的File,构造Document对象.
     * 该方法利用输入参数file构造出AbstractTermTupleStream子类对象后,内部调用
     *      AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream)
     * @param docId     : 文档id
     * @param docPath   : 文档绝对路径
     * @param file      : 文档对应File对象
     * @return          : Document对象
     * </pre>
     */
    @Override
    public AbstractDocument build(int docId, String docPath, File file) {
        AbstractDocument document = null;
        AbstractTermTupleStream ts = null;
        try {
            ts= new TermTupleScanner(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
            //再加上停用词过滤器
            ts = new StopWordTermTupleFilter(ts);
            //再加上正则表达式过滤器
            ts = new PatternTermTupleFilter(ts);
            //再加上单词长度过滤器
            ts = new LengthTermTupleFilter(ts);
            document = build(docId,docPath,ts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            ts.close();
        }
        return document;
    }




    /**
     * <pre>
     * 由解析文本文档得到的TermTupleStream,构造Document对象.
     * @param docId             : 文档id
     * @param docPath           : 文档绝对路径
     * @param termTupleStream   : 文档对应的TermTupleStream,三元组流
     * @return ：Document对象
     * </pre>
     */
    @Override
    public  AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream){
        //创建Document
        AbstractDocument document = new Document(docId,docPath);
        //从三元组流中取下一个三元组
        for(AbstractTermTuple termTuple = termTupleStream.next();termTuple!=null;termTuple = termTupleStream.next()) {
            document.addTuple(termTuple);
        }

        return document;
    }


}
