package com.jboss.advocacy;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.jboss.advocacy.model.*;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsMain {

    public static final void main(String[] args) {
        try {

        	// load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase();
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            
            // Setup
            Room kitchen = new Room( "kitchen" );
            Room bedroom = new Room( "bedroom" );
            Room office = new Room( "office" );
            Room livingRoom = new Room( "livingroom" );

            ksession.insert( kitchen );
            ksession.insert( bedroom );
            ksession.insert( office );
            ksession.insert( livingRoom );

            Sprinkler kitchenSprinkler = new Sprinkler( kitchen );
            Sprinkler bedroomSprinkler = new Sprinkler( bedroom );
            Sprinkler officeSprinkler = new Sprinkler( office );
            Sprinkler livingRoomSprinkler = new Sprinkler( livingRoom );
            
            ksession.insert( kitchenSprinkler );
            ksession.insert( bedroomSprinkler );
            ksession.insert( officeSprinkler );
            ksession.insert( livingRoomSprinkler );
            
            // go !
            ksession.fireAllRules();
 
            // create two fires
            Fire kitchenFire = new Fire( kitchen );
            Fire officeFire = new Fire( office );

            FactHandle kitchenFireHandle = ksession.insert( kitchenFire );
            FactHandle officeFireHandle = ksession.insert( officeFire );

            ksession.fireAllRules();

            // put out the fires
            ksession.retract( kitchenFireHandle );
            ksession.retract( officeFireHandle );

            ksession.fireAllRules();
            
            logger.close();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("fireAlarmJava.drl"), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }


}
