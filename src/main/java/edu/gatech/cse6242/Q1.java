package edu.gatech.cse6242;

import java.io.IOException;
import java.lang.Object;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {

    public static class MyMapper 
            extends Mapper<Object, Text, IntWritable, IntWritable> {
        
        private IntWritable weight = new IntWritable();
        private IntWritable node = new IntWritable();

        public void map(Object key, Text value, Context context
                       ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
            while (itr.hasMoreTokens()){            
                String lineList = itr.nextToken();
                String line[] = lineList.split("\t");      
                node.set(Integer.parseInt(line[0]));
                int w = Integer.parseInt(line[2]);
                if (w > 0){    
                    weight.set(w);       
                    context.write(node, weight);
                }
            }
        }
    }    

    public static class MyMax extends 
            Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        private IntWritable result = new IntWritable();
        
        public void reduce(IntWritable key, Iterable<IntWritable> values,
                            Context context
                          ) throws IOException, InterruptedException {
            int su = 0;
            for (IntWritable val : values) {
                if (su < val.get()) {
                    su = val.get();
                }
            }
            result.set(su);
            context.write(key, result);
        }
    }   
    

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    /* TODO: Needs to be implemented */
    job.setJarByClass(Q1.class);
    job.setMapperClass(MyMapper.class);
    
    job.setCombinerClass(MyMax.class);
    job.setReducerClass(MyMax.class);
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(IntWritable.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
