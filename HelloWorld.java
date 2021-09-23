import java.util.Date;
public class HelloWorld {
    int x; 
    int y;
    public static HelloWorld global;

    public static void main(String[] args) {
        HelloWorld a1 = new HelloWorld();
        HelloWorld a2 = new HelloWorld2();
        HelloWorld a3 = new HelloWorld2();
        HelloWorld a4 = new HelloWorld3();
        HelloWorld a5 = new HelloWorld4();
        HelloWorld a6 = new HelloWorld4();
        HelloWorld a7 = new HelloWorld6();
        HelloWorld a8 = new HelloWorld7();
        HelloWorld a9 = new HelloWorld8();
        HelloWorld a10 = new HelloWorld9();


        // Checking for redundant framestate node.
//       
//        
//        //1. Static call  
//        for(int i = 0; i < 1000000; i++) {
//            HelloWorld.staticcall();
//            System.out.println("Return value : "+ i);
//        }
//        int m = 20;
//        for(int i = 0; i < 1000000; i++) {
//            HelloWorld o1 =  a1.notinline(i);
//            if(i % 2== 0){
//               int a = a1.framestatechecking(o1);
//               System.err.println("Return value"+ a);
//           }
//            else {
//               int a = a1.framestatechecking(o1);
//               System.err.println("Return value"+ a);
//            }
//        }


        int x1 = a2.escapingobj(a1);


        for(int i = 0; i < 10000000; i++) {
            if(i % 2== 0){
               int a = a1.framestatechecking(a2);
               System.err.println("Return value"+ a);
           } else if(i %3 == 0){
               int b = a2.framestatechecking(a3);
               System.err.println("Return value"+ b);
           }
           else if(i % 5 == 0){
               int c = a3.framestatechecking(a4);
               System.err.println("Return value"+ c);
           }else if(i %7 == 0){
               int b = a4.framestatechecking(a5);
               System.err.println("Return value"+ b);
           }
           else if(i % 11 == 0){
               int c = a5.framestatechecking(a6);
               System.err.println("Return value"+ c);
           }
           else if(i % 13 == 0){
               int c = a6.framestatechecking(a7);
               System.err.println("Return value"+ c);
           }else if(i %17 == 0){
               int b = a7.framestatechecking(a8);
               System.err.println("Return value"+ b);
           }
           else if(i % 19 == 0){
               int c = a8.framestatechecking(a9);
               System.err.println("Return value"+ c);
           }
           else if(i % 23 == 0){
               int c = a9.framestatechecking(a10);
               System.err.println("Return value"+ c);
           }
        }
//
//
//        }
//        for(int i = 0; i < 10000000; i++) {
//           int a = a1.framestatechecking(a2);
//           System.out.println(a);
//        }

//        int b  = a3.framestatechecking(a2);
//        System.out.println("Return value"+ a3.x);
//        int c  = a4.framestatechecking(a2);
//        System.out.println("Return value"+ a3.x);

//        
//        // Checking for pure method
        
//        int m = 2;
//        for(int i = 0; i < 10000000; i++) {
//            if(i%2==0){
//               int a = HelloWorld.puremethod(m);
//               System.out.println("Return value"+ a);
//           }     
//        }
//
//        // Checking for non esacping object
//
//        for(int i = 0; i < 10000000; i++) {
//            if(i%2==0){
//               int a = HelloWorld.nonescapingobj(a1);
//               System.out.println("Return value"+ a);
//           }     
//        }
//
//        //checking for escaping object
//        
//        for(int i = 0; i < 10000000; i++) {
//            if(i%2==0){
//               int a = HelloWorld.escapingobj(a1);
//               System.out.println("Return value "+ a1.x);
//           }     
//        }
        
        // Virtual Calls

//        HelloWorld a2 = new HelloWorld();
//        int a = HelloWorld.escapingobj(a1);
//
//        for(int i = 0; i < 10000000; i++) {
//            if(i%2==0){
//               int b = a1.framestatechecking(a2);
//               System.out.println("Return value"+ a2.x);
//           }     
//        }

    } 

    
    HelloWorld notinline(int i){
        if(i % 3 == 0){
            HelloWorld p1 = new HelloWorld();
            return p1;
        }
        else if(i % 2 == 0) {
            HelloWorld p2 = new HelloWorld3(); 
            return p2;
        }
        else {
            HelloWorld p3 = new HelloWorld4(); 
            return p3;
        }
    }
    static void staticcall() {
        int i = 10;
        int j = 20;
        HelloWorld o1 = new HelloWorld();
        o1.x = i * j;
        HelloWorld o2 = new HelloWorld();
        forint t = 0; t < 10000000; t++) {
            int k  = o1.escapingobj(o2);
        }
        o1.y = o2.x;
    }

    int framestatechecking(HelloWorld p1) {
        int i = 1;
        int j = 1;
        p1.x = 2;
        int k = p1.x;
        int l = 3;
        p1.y = 4; 
        HelloWorld a3 = new HelloWorld();
        int b = p1.escapingobj(a3);
        return b * 2;
    }
    
    int puremethod(HelloWorld p2) {
        int a = 50;
        int b = 100;
        HelloWorld o4 = new HelloWorld();
        o4.x = a + b;
        return o4.x * p2.y;
    }
    
    static int nonescapingobj(){
        HelloWorld o2 = new HelloWorld();
        o2.x = 2;
        o2.y = 3;
        return o2.x;
    }

    int escapingobj(HelloWorld p1){
        p1.x = 5;
        p1.y = 6;
        HelloWorld2 o3 = new HelloWorld2();
        global = p1;
        p1 = o3;
        return p1.x;
    }

}

class  HelloWorld2 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p4) {
        int i = 1;
        int j = 1;
        p4.x = 2;
        int k = p4.x;
        int l = 3;
        p4.y = 4;
        return p4.y + 4;
    }
    
    int puremethod(HelloWorld p2){
        int a = 50;
        int b = 100;
        HelloWorld o4 = new HelloWorld();
        o4.x = a + b;
        return o4.x;
    }

    int nonescapingobj(HelloWorld p2){
        p2 = new HelloWorld();
        p2.x = 2;
        p2.y = 3;
        return p2.x;
    }

//    int escapingobj(HelloWorld p3){
//        global = p3;
//        p3.x = 2;
//        p3.y = 3;
//        return p3.x;
//    }
}
//
class  HelloWorld3 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}

//
//
class  HelloWorld4 extends HelloWorld {
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}

class  HelloWorld6 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}

class  HelloWorld7 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}

class  HelloWorld8 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}

class  HelloWorld9 extends HelloWorld {     
    
    int framestatechecking(HelloWorld p5) {
        int i = 1;
        int j = 1;
        int k = 2;
        HelloWorld o3 = new HelloWorld();
        o3.escapingobj(p5);
        p5.y = 2;
        int l = 3;
        p5.x = 4;
        return p5.y + 4;
    }
    int escapingobj(HelloWorld p7){
        global = p7;
        p7.x = 2;
        p7.y = 3;
        return p7.x;
    }

}
