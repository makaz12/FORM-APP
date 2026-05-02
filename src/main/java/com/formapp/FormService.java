package com.formapp;

import java.util.Optional;
import java.util.Scanner;
import java.util.List;
import java.util.UUID;


public class FormService{
    public static Scanner in = new Scanner(System.in);

    private static boolean required(){
        String answer = in.next().toLowerCase();
        boolean isRequired;
        //is it required?
        if(answer == "y"){
            isRequired = true;
        }
        else{
            isRequired = false;
        }
        return isRequired;
    }
    private static Form createForm(String name, String desc, List<Field> fields){
        
        Form newForm = new Form(name, desc, fields);
        //save the form
        return newForm;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int input = in.nextInt();
        switch (input) {
            case 1:
                String name = in.next();
                String desc = in.next();
                List<Field> fields = null;
                //do you want to add a field?
                if(fields.size() == 0){
                    String anotherField;
                        do{
                            String label = in.next();
                            String type = in.next();
                            required();
                            String constrain = in.next();
                            Field field = new Field(label, type, required(), constrain);
                            fields.add(field);
                            //do you want to add another field?
                            anotherField = in.next().toLowerCase();
                    }while(anotherField == "y");
                }
                createForm(name, desc, fields);
                break;
            case 2:
                
                break;
        
            default:
                break;
        }
    }
}
