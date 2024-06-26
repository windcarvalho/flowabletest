/**
 * dependências:
 *  commons-lang3-3.14.0.jar
 *  jcl-over-slf4j-2.0.13.jar
 *  jgraphx-1.10.4.1.jar
 *  flowable-bpmn-converter-7.0.1.jar
 *  flowable-bpmn-layout-7.0.1.jar
 *  flowable-bpmn-model-7.0.1.jar
 *  flowable-engine-common-api-7.0.1.jar
 *  slf4j-api-2.0.13.jar
 *  slf4j-simple-2.0.13.jar
 */
package com.example.demo.service;

import java.io.File;
import java.io.FileWriter;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ExclusiveGateway;
// new
import org.flowable.bpmn.BpmnAutoLayout;

import java.util.ArrayList;
import java.util.List;

public class GenFlowableFlowsV2 {

	static double LAST_X = 50;
	static double LAST_Y = 100; 
	static double WIDTH = 70;
	static double HEIGHT = 50;

	public static List<Integer> wayPoint(int[] coords) {
		ArrayList<Integer> ai = new ArrayList<Integer>();
		for (int i : coords)
			ai.add(i);

		return ai;
	}

	public static void positionAndSkip2(BpmnModel model, final FlowElement fe, boolean changeY) {}
	public static void positionAndSkip(BpmnModel model, final FlowElement fe, boolean changeY) {
		
		
		GraphicInfo gi = new GraphicInfo();

		gi.setX(LAST_X);
		gi.setY(LAST_Y);
		gi.setWidth(WIDTH);
		gi.setHeight(HEIGHT);

		
		if(changeY){
			LAST_Y = LAST_Y + 2*HEIGHT;
			System.out.println("mudou o Y:"+ LAST_Y);
			LAST_X = 50;
		}
		LAST_X += 2 * WIDTH;
		System.out.println("mudou o X:"+ LAST_X);
		if (fe instanceof EndEvent) {
			System.out.println("Aqui no fim");
			gi.setY(LAST_Y);
		}

		if (fe instanceof EndEvent || fe instanceof StartEvent || fe instanceof Gateway) {
			gi.setWidth(HEIGHT);
			gi.setHeight(HEIGHT);
		}

		
		gi.setElement(fe);
		model.addGraphicInfo(fe.getId(), gi);

		
	}

	public static BpmnModel genBigBoy( int tasks, int vars, int varLength)
			throws Exception
	{
		
		 BpmnModel bpmnModel = new BpmnModel();
		 Process process = new Process();
		    bpmnModel.addProcess(process);
		    process.setId("sequentialProcess_"+tasks);
		    process.setName(tasks+" tasks");
			
		
        // Adicionando elementos ao processo
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        
        process.addFlowElement(startEvent);        
        positionAndSkip( bpmnModel, startEvent, false );

		// final node
        EndEvent endEvent = new EndEvent();
        endEvent.setId("finish");       
        //positionAndSkip(bpmnModel, endEvent, false);
        //bpmnModel.getGraphicInfo(endEvent.getId()).setY(0);
        
        
        UserTask lastUserTask = null;
        ArrayList<SequenceFlow> edges = new ArrayList<SequenceFlow>();  
        for( int t=1; t<=tasks; t++ )
   		 {
   			 final String name = "userTask_" + t;
   			 
   			 if( lastUserTask == null )
   			 {
   				// connects to start node
   				lastUserTask = new UserTask();
   				lastUserTask.setId(name);
   				lastUserTask.setName("User Task " + t);
				lastUserTask.addAttribute(null);				
   	   			process.addFlowElement(lastUserTask);
				
   				
   				//
   				//process.addFlowElement(new SequenceFlow("start", name ));
   	   			edges.add(new SequenceFlow("start", name )); 
				positionAndSkip(bpmnModel, lastUserTask, false);
   			 }
   			 else
   			 {

				// adiciona uma decisão antes da tarefa 2
				if(t==2){

				UserTask currUserTask = new UserTask();
    			currUserTask.setId(name);
    			currUserTask.setName("User Task " + t);				
    	   		process.addFlowElement(currUserTask);
				lastUserTask = currUserTask;

				//criando o gateway
				String gid = "gw_" + (t-1);
				ExclusiveGateway gate = new ExclusiveGateway();
				gate.setId(gid);
				gate.setName("Pass " + (t-1) +"?");
				gate.setExclusive(true);
				
				process.addFlowElement(gate);
				
				positionAndSkip(bpmnModel, gate, false);
				process.addFlowElement(new SequenceFlow("userTask_1", gid)); //Gateway saindo da primeira Task
				
				SequenceFlow pass = new SequenceFlow( gate.getId(), lastUserTask.getId() ); //yes vai para segundo task
				   pass.setName("yes");
				   pass.setConditionExpression("${pass}");
				   gate.setDefaultFlow(pass.getId()); //caminho default
				SequenceFlow fail = new SequenceFlow( gate.getId(), endEvent.getId() ); //vai pro fim do processo
				   fail.setName("no");				   
				   fail.setConditionExpression("${!pass}");
				
				   edges.add(pass);
				edges.add(fail);				
			
		   } else{
   				 // connects to prior node
   				 // connects to start node
    			UserTask currUserTask = new UserTask();
    			currUserTask.setId(name);
    			currUserTask.setName("User Task " + t);
				//currUserTask.
    	   		process.addFlowElement(currUserTask);
    				
    			//
    	   		//process.addFlowElement(new SequenceFlow(lastUserTask.getId(), name ));
    	   		edges.add(new SequenceFlow(lastUserTask.getId(), name ));
    	   		lastUserTask = currUserTask;
				

				if(t % 10 == 0){
					positionAndSkip(bpmnModel, lastUserTask, true);
				}else{
					positionAndSkip(bpmnModel, lastUserTask, false);
				}
   			 }
			}
   			//positionAndSkip(bpmnModel, lastUserTask);
   			 
   			 

			  
   			 // Adds node and end event
   			//lastUserTask = new UserTask();
   			//lastUserTask.setId("userTask_"+(d+1));
   			//lastUserTask.setName("User Task" + (d+1));
   			//process.addFlowElement(lastUserTask);
   			//positionAndSkip(bpmnModel, lastUserTask);
   			// 
   			//EndEvent fin= new EndEvent();
   			//fin.setId("fim_" + d);
   			//process.addFlowElement(fin);
   			//positionAndSkip(bpmnModel, fin);
   			
   		
   		}
		 
        
        
        for( SequenceFlow sf : edges )
        {             
        	process.addFlowElement( sf );
			
        	
        }
        
        process.addFlowElement(new SequenceFlow(lastUserTask.getId(), endEvent.getId() ));
		process.addFlowElement(endEvent);
		positionAndSkip(bpmnModel, endEvent, false);
		 return bpmnModel;
	}

	public static void main(String[] args)
			throws Exception {

		
		
		  int[] empId = {5, 10, 25,50, 100,200,300,500,1000};
		  
		 		System.out.println("Generating big flows on Flowable");
		for (int fg : empId)
			try {
				long before = System.currentTimeMillis();
				// BpmnModel flow = genBigBoy(fg,10,10);
				BpmnModel flow = genBigBoy(fg, 1, 1);
				before = System.currentTimeMillis() - before;
				System.out.println("generated for " + fg + " in " + before + "ms");

				// TODO implement this and measure its timing...
				before = System.currentTimeMillis();
				BpmnAutoLayout layout = new BpmnAutoLayout(flow);
				layout.execute();
				System.out.print("layout in " + (System.currentTimeMillis() - before) + "ms");

				BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
				byte[] data = bpmnXMLConverter.convertToXML(flow);

				String xml = new String(data);
				// System.out.println( xml );

				// do you wanna save it?
				// File file = new File("flowable-big-boy-fg"+ fg +".bpmn");
				File file = new File("sequentialFlow_" + fg + ".bpmn");
				FileWriter fw = new FileWriter(file);
				fw.write(xml);
				fw.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

	}

}
