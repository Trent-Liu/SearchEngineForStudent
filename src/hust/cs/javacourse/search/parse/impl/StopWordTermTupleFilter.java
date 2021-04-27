package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopWordTermTupleFilter extends AbstractTermTupleFilter {

    private List<String> stopWord;

    /**
     * 构造函数
     * @param input : 输入流
     * */
    public StopWordTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
        //创建停用词列表
        this.stopWord = new ArrayList<String>(Arrays.asList(StopWords.STOP_WORDS));
    }

    /**
     * 获得下一个三元组
     *过滤掉停用词
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple stopWordFilter = input.next();
        if(stopWordFilter==null) {
            return null;
        }
        //如果当前读取到的三元组的单词是停用词表中的，则读取下一个
        while (stopWord.contains(stopWordFilter.term.getContent())){
            stopWordFilter = input.next();
            //读取到末尾
            if(stopWordFilter==null) {
                return null;
            }
        }
        return stopWordFilter;
    }
}
