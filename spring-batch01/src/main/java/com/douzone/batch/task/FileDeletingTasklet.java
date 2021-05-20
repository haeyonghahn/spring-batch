package com.douzone.batch.task;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

public class FileDeletingTasklet implements Tasklet {

	private Resource directory;
	
	public Resource getDirectory() {
		return directory;
	}

	public void setDirectory(Resource directory) {
		this.directory = directory;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {		
		try {
			File dir = directory.getFile();
			File[] files = dir.listFiles();
			if(files.length > 0) {				
				for(File file : files) {
					boolean deleted = file.delete();
					if(!deleted)
						throw new Exception("파일이 삭제되지 않았습니다. " + file.getPath());
					else 
						System.out.println("파일이 삭제되었습니다. " + file.getPath());
				}
			}
			else {
				System.err.println("삭제할 파일이 없습니다. ---------------------------");
			}
		} catch(Exception e) {
			throw e;
		}	
		return RepeatStatus.FINISHED;
	}
}
