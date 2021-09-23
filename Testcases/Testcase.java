class Testcase {
    int x;
    public static void main(String args[]){
        System.err.println("Main Class");
        Testcase o1 = new Testcase();
        Testcase o2 = new Testcase();
        int i = 1000000;
        while(i>0) {
            if(i%2 ==0) {
                o2 = new Testcase();
            }
            else{
                o2 = new childclass();
            }
            o2.x = i;
            o1.foo(o2);
            i--;
        }
        System.err.println("Value after function call : "+ o2.x);
        o1.bar();
    }

    void foo(Testcase p1){
        Testcase o5; // = new childclass();
        Testcase o6 = new Testcase();
//        if(p1.x%2== 0) {
//            o5 = new Testcase(); 
//        }
//        else {
//            o5 = new childclass();
//        }
        p1.bar();
        int y = 10;
        int z = p1.x + y;
        //System.err.println("Value of z is : "+ z);
    }
    
    void bar(){
        Testcase o4 = new Testcase();
        Testcase o5 = new Testcase();
        o4.fb(o5);        
        System.err.println("Value of o4 after function call : "+ o5.x);
    }

    void fb(Testcase p2) {
        p2.x = 20;
        p2.foobar();
    }

    void foobar(){
        Testcase o6 = new Testcase();
        System.err.println("Last Method");
        o6.x = 10;
        System.err.println("Value of o5 is : "+ o6.x);
    }

}

class childclass extends Testcase {
    int y;

    void foo(Testcase p1){
        p1.x =10;
        Testcase o5 = new childclass();
        if(p1.x%2== 0) {
            p1.bar();
        }
        else {
            o5.bar();
        }
        int y = 10;
        int z = p1.x + y;
    }
    void bar(){
        childclass o7 = new childclass();
        o7.fb2(o7);
        System.err.println("Value of o7 after call : "+ o7.y);
    }
    void fb2(childclass p3){
        p3.y =30;
        if(p3.y%2==0){
            p3.y = p3.y + 20;
            System.err.println("Value of p3.y : "+ p3.y);
        }
        else {
            System.err.println("Not true");
        }
    }

}
