 /* MutationApp : implements quiver mutation according to Fomin-Zelevinsky
 * 
 * Bernhard Keller, October 17, 2006
 * Updated on March 26, 2018
 *
 */

//package MutationApp;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.math.*;
import java.util.*;
import com.perisic.ring.*;
import java.awt.print.*;
import javax.jnlp.*;
import Jama.*; 
import java.applet.*;
import java.applet.AudioClip;
import java.net.URL;
import java.net.MalformedURLException;


class Permutation {
    int n;
    int[] perm;
    
    /** Creates a new instance of Permutation */
    public Permutation(int[] argperm) {
        perm=argperm;
        n=perm.length;
    }
    
    public Permutation(int argn){
        n=argn;
        perm=new int[n];
        for (int i=0; i<n; i++){
            perm[i]=i;
        }
    }
    
    public Permutation(String str){
        //if ((str==null) || (str.length()==0)){}
        String[] fields=str.split(" ");
        n=fields.length;
        perm=new int[n];
        for (int i=0; i<n; i++){
            perm[i]=Integer.parseInt(fields[i])-1;
        }
    }
    
    public Permutation inverse(){
        int[] q=new int[n];
        int i,j;
        //System.out.println("Direct: "+ permToString(p));
        for (i=0; i<n; i++){
            j=0;
            while ((j<n) && (perm[j]!=i)){
                j++;
                }
            q[i]=j;
        }
        //System.out.println("Inverse: "+permToString(q));
        return new Permutation(q);
    }
    
    public int[] getlist(){
        return perm;
    }
    
    public Permutation compose(Permutation P){
        int[] p=perm;
        int[] q=P.getlist();
        int[] r=new int[n];
        for (int i=0; i<n; i++){
            r[i]=p[q[i]];
        }
        return new Permutation(r);
    }
    
    public void set(int i, int j){
        perm[i]=j;
    }
    
    public int get(int i){
        return perm[i];
    }
    
    public static Permutation sigma(int argn, int i){
        Permutation P=new Permutation(argn);
        P.set(i,i+1);
        P.set(i+1,i);
        return P;
    }

    public Permutation next(){
        int[] seq=toseq();
        seq=nextseq(seq);
        if (seq!=null){
            return seqtoperm(seq);}
        else {return null;}
    }

    public static int[] nextseq(int[] seq){
        int n=seq.length;
        int i,j;
        int[] s=new int[n];

        i=n-1;
        while ((i>=0) && (seq[i]==n-i-1)){
            i--;
        }

        if (i<0){return null;}

        for (j=0; j<i; j++){
            s[j]=seq[j];
        }

        s[i]=seq[j]+1;

        for (j=i+1;j<n;j++){
            s[j]=0;
        }

        return s;

    }

    public int[] toseq(){
        int[] seq=new int[n];
        boolean[] taken= new boolean[n];
        int i,j, cter;

        for (i=0; i<n; i++){
            taken[i]=false;
        }


        for (i=0; i<n; i++){
            cter=0;
            for (j=0; j<(perm[i]+1); j++){
                if (!taken[j]){
                    cter++;
                }
            }
            seq[i]=cter-1;
            taken[perm[i]]=true;
        }
        return seq;
    }

    public static Permutation seqtoperm(int[] seq){
        int n=seq.length;
        boolean[] chosen=new boolean[n];
        int[] p=new int[n];
        int i, j, r, cter;

        for (i=0; i<n; i++){
            chosen[i]=false;
        }

        for (i=0; i<n; i++){
            //System.out.println("n="+n+", i="+i+" seq[i]="+seq[i]);
            cter=0;
            j=0;
            while ((cter<seq[i]) || (chosen[j])) {
                if (!chosen[j]){
                    cter++;
                }
                j++;
            }
            //System.out.println("Assigning: p["+i+"]="+j+"chosen["+j+"]=true");
            p[i]=j;
            chosen[j]=true;
        }
        return new Permutation(p);
    }
    
    public static Permutation rand(int argn){
        int n=argn;
        boolean[] chosen=new boolean[n];
        int[] p=new int[n];
        int i, j, r, cter;
        
        for (i=0; i<n; i++){ 
            chosen[i]=false;
        }
        
        for (i=0; i<n; i++){
            r=(int) Math.floor(Math.random()*(n-i));
            //System.out.println("n="+n+", i="+i+" r="+r);
            cter=0;
            j=0;
            while ((cter<r) || (chosen[j])) {
                if (!chosen[j]){
                    cter++;
                }
                j++;
            }
            //System.out.println("Assigning: p["+i+"]="+j+"chosen["+j+"]=true");
            p[i]=j;
            chosen[j]=true;
        }
        return new Permutation(p);
    }
    
    public String toString(){
        String s="";
        for (int i=0; i<n; i++){
            s=s+(perm[i]+1);
            if (i<n-1){
                s=s+" ";
            }
        }
        return s;
    }
    
    public int firstinversion(){
        int firstinv=-1;
        for (int i=0; i<n-1;i++){
            if (perm[i]>perm[i+1]){
                firstinv=i;
                continue;
            }
        }
        return firstinv;
    }
    
    public String redword(){
        String s="";
        Permutation P=new Permutation(perm);
        Permutation Q=null;
        int i=P.firstinversion();
        //System.out.println("Permutation: "+P);
        //System.out.println("First inversion: "+ (i+1));
        while (i>-1) {
            if (s.length()>0){s=s+" ";}
            s=s+(i+1);
            Q=Permutation.sigma(n,i);
            //System.out.println("Transposition "+(i+1)+ " :" +Q);
            P=P.compose(Q);
            i=P.firstinversion();
        }
        return s;
    }
    
}



class MoveablePoint extends Point2D.Float implements Cloneable {
    float dx, dy;
    boolean fixed;
    int r;
    int height;
    boolean frozen;
    boolean marked;
    String label;
    String content;
    Color mycolor;
    //Shape shape;

    public Object clone(){
        MoveablePoint mp=(MoveablePoint) super.clone();
        mp.label=new String(label);
        mp.fixed=fixed;
        mp.r=r;
        mp.height=height;
        mp.frozen=frozen;
        mp.marked=marked;
        mp.content=content;
        if (mycolor!=null){
            mp.mycolor=new Color(mycolor.getRGB());
            }
        else {
            mp.mycolor=null;
        }
        return mp;
    }

    public void setColor(Color col){
        mycolor=col;
    }
    public void read(BufferedReader in){
	String str;
	String patternstr=" ";
	String[] fields;
        int red, green, blue;
	try{
	    str=in.readLine();
	    //System.out.println(str);
	    fields=str.split(patternstr);
	    r=Integer.parseInt(fields[0]);
	    float x=java.lang.Float.parseFloat(fields[1]);
	    float y=java.lang.Float.parseFloat(fields[2]);
            setLocation(x,y);
            frozen=false;
            setColor(Color.red);
            if (fields.length>3){
                int isfrozen=Integer.parseInt(fields[3]);
                if (isfrozen>0){frozen=true;} else {frozen=false;}
            }
            if (fields.length>5){
                if (fields[4].equals("rgb")){
                    red=Integer.parseInt(fields[5]);
                    green=Integer.parseInt(fields[6]);
                    blue=Integer.parseInt(fields[7]);
                    mycolor=new Color(red,green,blue);
                }
            }
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }


    public void write(BufferedWriter out){
	try{
	    out.write(r+" ");
            int isfrozen;
            if (frozen){isfrozen=1;} else {isfrozen=0;}
              if ((mycolor.equals(Color.RED))||(mycolor.equals(Color.BLUE))){
                out.write(getX()+" " + getY()+ " " + isfrozen);
              }
              else {
                out.write(getX()+" " + getY()+ " " + isfrozen+ " rgb "+mycolor.getRed()+" "+mycolor.getGreen()+" "+mycolor.getBlue());
              }
            out.newLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }

    
        public MoveablePoint(int x, int y) {
        super(x, y);
        frozen=false;
        marked=false;
        fixed=false;
        mycolor=Color.red;
        r=9;
        height=0;
        label="";
        setLocation(x, y);
    }
        
        public MoveablePoint(int x, int y, int vertradius) {
        super(x, y);
        frozen=false;
        marked=false;
        fixed=false;
        mycolor=Color.red;
        label="";
        setLocation(x, y);
        setradius(vertradius);
        height=0;
    }

    void setLocation(int x, int y) {
        super.setLocation(x, y);
        //shape = new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
    }
    
    void setHeight(int h){
        height=h;
    }
    
    int getHeight(){
        return height;
    }
    
    public Color getColor(){
        return mycolor;
    }
    
    public void setLabel(String s){
        label=new String(s);
    }
    
    public void setContent(String s){
        content=new String(s);
    }
    
    public void showHeight(){
        label=""+height;
    }
    
    public void deleteLabel(){
        label="";
    }
    
    public void deleteContent(){
        content=null;
    }
    
    void setradius(int rad){
        r=rad;
        //shape=null;
        //shape = new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
    }
    
    int getradius(){
        return r;
    }
    
    public boolean hit(int x, int y) {
	    Shape sh=getShape();
            return sh.contains(x, y);
    }

        public Shape getShape() {
        return new Ellipse2D.Float(x - r, y - r, 2*r, 2*r);
    }

    public void togglePhase(){
    if (frozen){frozen=false;}
    else {frozen=true;}
    }
    
    public void toggleMarking(){
        if (marked){marked=false;}
        else {marked=true;}
    }
    
    public void setFrozen(boolean state){
        frozen=state;
    }
    
    public void draw(Graphics2D g){
       if (frozen){g.setPaint(Color.blue);}
        else{g.setPaint(mycolor);}
       if (marked){g.setPaint(Color.yellow);}
        Shape sh=getShape();
        g.fill(sh);
        g.setPaint(Color.black);
        g.draw(sh);
    }
    public void draw(Graphics2D g, String t){
       //System.out.println("Draw with label called");
        draw(g);
        String s;
        if (content==null){
            s=t;
        }
        else {
            s=content;
        }
        g.setPaint(Color.black);
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(s, g);
        double w=rect.getWidth();
        double h=rect.getHeight();
        g.drawString(s, (float) (getX()-w/2), (float) (getY()+h/3));
        if (!label.equals("")){
            s=label;
            rect = fm.getStringBounds(s, g);
            w=rect.getWidth()*2;
            h=rect.getHeight();
            double tx=getX()+1.5*r;
            double ty=getY()-1.5*r;
            tx=tx-w/2;
            ty=ty-h/2;
            g.setPaint(Color.white);
            rect.setRect(tx,ty,w,h);
            g.fill(rect);
            g.setPaint(Color.black);
            g.drawString(s, (float)(tx+w/4), (float) (ty+0.75*h));
        }
    }
}

abstract class Mutable {
    int size;
    String Type;
    int idnber;
    boolean isMutable;
    
    public BigInteger[] data(int k){
        return null;
    }
    
    public boolean getIsMutable(){
        return isMutable;
    }
    
    public void setIsMutable(boolean im){
        isMutable=im;
    }
    
    public int getIdNber(){
        return idnber;
    }
    
    public void setIdNber(int nb){
        idnber=nb;
    }
    
    public String getType(){
        return Type;
    }
    
    abstract void mutate(Quiver Quiv, int k);
    
    void mutate(Quiver Quiv, int k, int dir){
        mutate(Quiv, k);
    }


    public void permuteVertices(int[] perm){

    }


    abstract String toString(int i);
    
    public String toString(){
        int i;
        String s;
        
        s="";
        for (i=0; i<size; i++){
            s=s+toString(i);
        }
     return s;   
    }
}

class JVPointDouble{
    double[] coords;

static public JVPointDouble barycenter(JVPointDouble P1, double w1, JVPointDouble P2, double w2){
    double[] coords=new double[P1.coords.length];
    for (int i=0; i<coords.length;i++){
        coords[i]=P1.coords[i]*w1+P2.coords[i]*w2;
    }
    return new JVPointDouble(coords);
}
    
    public JVPointDouble(double[] argcoords){
        coords=new double[argcoords.length];
        for (int i=0; i<coords.length; i++){
            coords[i]=argcoords[i];
        }
    }
    
    public JVPointDouble(double argx, double argy, double argz){
        coords=new double[3];
        coords[0]=argx;
        coords[1]=argy;
        coords[2]=argz;
    }
    
    public String toString(){
        String s=coords[0]+"";
        for (int i=1;i<coords.length;i++){
            s=s+" "+coords[i];
        }
        return s;
    }
    
    
}

class JVPoint{
    BigInteger[] coords;
    
    public double getCoord(int i){
        return coords[i].doubleValue();
    }
    
    public double getX(){
        return coords[0].doubleValue();
    }
    
    public double getY(){
        return coords[1].doubleValue();
    }
    
    public double getZ(){
        return coords[2].doubleValue();
    }
    
    public String tojvx(){
        String s="<p>"+coords[0].toString();
        for (int i=1; i<coords.length;i++){
            s=s+" "+coords[i].toString();
        }
        s=s+"</p>\n";
        return s;
    }
    
    public JVPoint(BigInteger[] arg){
        coords=new BigInteger[arg.length];
        for (int i=0; i<coords.length; i++){
            coords[i]=new BigInteger(arg[i].toString());
        }
    }
    
    public boolean equals(Object o){
        JVPoint p=(JVPoint) o;
        if (p.coords.length!=coords.length){
            return false;
        }
        for (int i=0; i<coords.length; i++){
            if (!coords[i].equals(p.coords[i])){
                return false;
            }
        }
        return true;
    }
    
    public String toString(){
        String s="["+coords[0];
        for (int i=1; i<coords.length;i++){
            s=s+","+coords[i];
        }
        s=s+"]";
        return s;
    }
}

class JVEdge{
    int from, to, targetvertex;
    static final double NEARNESS=0.8;

    public double getfromweight(){
        if (targetvertex==from){
            return 1-NEARNESS;
        }
        else {
            return NEARNESS;
        }
    }

    public double gettoweight(){
        if (targetvertex==to){
            return 1-NEARNESS;
        }
    else {
            return NEARNESS;
            }
    }

    public void settargetvertex(int v){
        targetvertex=v;
    }

    public int gettargetvertex(){
        return targetvertex;
    }
    
    public JVEdge(int f, int t){
        if (f<=t){
            from=f; to=t;
        }
        else {
            from=t; to=f;
        }
        //System.out.println("New edge created from "+from+" to "+to);
    }
    
    public boolean equals(Object o){
        JVEdge e=(JVEdge) o;
        return (((from==e.to) & (to==e.from))||((from==e.from) & (to==e.to)));
    }
    
    public String toString(){
        return "Edge from: " + from +" to " +to+" target: "+targetvertex;
    }
}

class JVFace{
    int[] Vertices;
    
    public String tojvx(){
        String s="<f>"+Vertices[0];
        for (int i=1; i<Vertices.length; i++){
            s=s+" "+Vertices[i];
        }
        s=s+"</f>\n";
        return s;
    }
    
    public JVFace(int[] arg){
        Vertices=new int[arg.length];
        for (int i=0; i<Vertices.length; i++){
            Vertices[i]=arg[i];
        }
    }
    
    public boolean equals(Object o){
        JVFace f=(JVFace) o;
        if (Vertices.length!=f.Vertices.length){
            return false;
        }
        int[] v1=(int[]) Vertices.clone();
        int[] v2=(int[]) f.Vertices.clone();
        Arrays.sort(v1);
        Arrays.sort(v2);
        for (int i=0; i<v1.length; i++){
            if (v1[i]!=v2[i]){
                return false;
            }
        }
        return true;
    }
    
    public String toString(){
        String s="["+Vertices[0];
        for (int i=1; i<Vertices.length;i++){
            s=s+","+Vertices[i];
        }
        s=s+"]";
        return s;
    }
}
   
abstract class Searchable{
    public abstract boolean found();
    public abstract boolean toobig();
    public abstract void mutate(int i);
    
    public void traverseTree(int n, int last, String s, int nbpoints){
        System.out.println("n="+n+ " last="+last+" s="+s+" too big: "+toobig()+
                " found:"+found());
        
        
        if ((n==0)||found()||toobig()){return;}
        int i=0;
        while ((i<nbpoints)&&(!found())){
        if (i!=last){
            //System.out.println("Mutate forward at "+i);
            //mutate(i,q);
            mutate(i);
            traverseTree(n-1, i, s+" "+(i+1),nbpoints);
            //System.out.println("Mutate back    at "+i);
            //mutate(i,q);
            if (!found()){mutate(i);}
            }
         i=i+1;
        }
    }
}

class SourceSinkSearcher extends Searchable{
    Quiver q;
    int maxDepth, maxMult;
    boolean isFound;
    
    SourceSinkSearcher(int argmaxDepth, int argmaxMult,Quiver argq){
        maxDepth=argmaxDepth;
        maxMult=argmaxMult;
        q=argq;
    }
    
    public void mutate(int i){
        q.mutate(i,1);
    }
    
    public boolean found(){
        if (isFound){return true;}
        isFound=(q.sinksSources().length>0);
        return isFound;
    }
    
    public boolean toobig(){
        return q.maxMultExceeds(maxMult);
    }
}


class Tracker extends Mutable{
    Vector JVPoints;
    Vector JVFaces;
    Vector JVDualEdges;
    JVPoint LastPoint;
    JVFace LastFace;
    JVEdge LastDualEdge;
    Mutable Mut;
    GRvector grvect;
    boolean isnew;
    double[][] A;
    
    public void traverseTree(int n, int last, String s, Quiver q){
        System.out.println("Call of traverseTree with n="+n+ " last="+last+" s="+s);
        
        if (n==0){return;}
        for (int i=0; i<q.nbpoints; i++){
        if (i!=last){
            //System.out.println("Mutate forward at "+i);
            mutate(q, i);
            q.mutate(i,1);
            traverseTree(n-1, i, s+i,q);
            //System.out.println("Mutate back    at "+i);
            mutate(q, i);
            q.mutate(i,1);
            
            }
        }
    }
    
    public String tojvx(){
        int i,j;
        double[] v=new double[3];
        String s,str;
        
        s="<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>"+"\n";
        s=s+"<!DOCTYPE jvx-model SYSTEM \"http://www.javaview.de/rsrc/jvx.dtd\">"+"\n";
        s=s+"<jvx-model>"+"\n";
        s=s+"<version type=\"dump\">0.10</version>";
        s=s+"<title>Polyhedron</title>"+"\n";
        s=s+"<geometries>\n";
        s=s+"<geometry name=\"Polyhedron\">\n";
        s=s+"<pointSet point=\"show\" dim=\""+size+"\">\n";
        s=s+"<points num=\""+JVPoints.size()+"\">\n";
        
        for (i=0; i<JVPoints.size();i++){
            JVPoint p=(JVPoint) JVPoints.elementAt(i);
            //s=s+p.tojvx();
            
            for (j=0; j<size;j++){
                v[j]=p.getCoord(j);
            }
            
            
            /*
            double c=1/Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
            v[0]=c*v[0];
            v[1]=c*v[1];
            v[2]=c*v[2];
            */
           
            
            
            double[] w=new double[size];
            
            for (int r=0;r<size;r++){
                w[r]=0;
                for(int t=0; t<size; t++){
                    w[r]=w[r]+A[r][t]*v[t];
                }
            }
            
            
            //s=s+p.tojvx();
            s=s+"<p> "+w[0];
            
            double l=w[0]*w[0];        
            for ( j=1;j<size;j++){
                s=s+" "+w[j];
                l=l+w[j]*w[j];
            }
            s=s+"</p>\n";
          
            //System.out.println("Length of vector "+i+":"+l);
           
        }
        s=s+"<thickness>10.0</thickness>\n";
        s=s+"<color type=\"rgb\">255 0 0</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 255</colorTag>\n";				
        s=s+" </points>\n</pointSet>\n<faceSet edge=\"show\" face=\"show\">\n";
        s=s+"<faces num=\""+JVFaces.size()+"\">\n";
        
        
        for (i=0; i<JVFaces.size();i++){
            s=s+((JVFace) JVFaces.elementAt(i)).tojvx();
        }
        
        s=s+"<color type=\"rgb\">255 255 255</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 255</colorTag>\n";
        s=s+"<creaseAngle>3.2</creaseAngle>\n";
        s=s+"</faces>\n";
        
        s=s+"<edges>";
        s=s+"<thickness>4.0</thickness>\n";
        s=s+"<color type=\"rgb\">0 0 200</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 0</colorTag>\n";
        s=s+"</edges>\n";
        s=s+"</faceSet>\n";
        
        s=s+"<center visible=\"hide\">\n";
        s=s+"<p>0 0 0</p>\n";
        s=s+"</center>/n";
        s=s+"</geometry>\n";
        s=s+"</geometries>\n";
        s=s+"</jvx-model>\n";
		
        return s;
    }
    
    public String dualtojvx(){
        int i,j;
        double[] v=new double[3];
        String s,str;
        
        
        s="<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>"+"\n";
        s=s+"<!DOCTYPE jvx-model SYSTEM \"http://www.javaview.de/rsrc/jvx.dtd\">"+"\n";
        s=s+"<jvx-model>"+"\n";
        s=s+"<version type=\"dump\">0.10</version>";
        s=s+"<title>Polyhedron</title>"+"\n";
        s=s+"<geometries>\n";
        s=s+"<geometry name=\"Polyhedron\">\n";
        s=s+"<pointSet point=\"show\" dim=\""+size+"\">\n";
        s=s+"<points num=\""+JVPoints.size()+JVDualEdges.size()+"\">\n";
        
        
        JVPointDouble[] FaceCenter=new JVPointDouble[JVFaces.size()];
        
        for (i=0; i<JVFaces.size();i++){
            JVFace f=(JVFace) JVFaces.elementAt(i);
            int[] vert=f.Vertices;
            for (j=0;j<size;j++){
                v[j]=0;
                for (int k=0;k<vert.length;k++){
                   JVPoint p=(JVPoint) JVPoints.elementAt(vert[k]);
                   v[j]=v[j]+p.coords[j].doubleValue();
                }
                v[j]=v[j]/size;
            }
            double[] w=new double[size];
            
            for (int r=0;r<size;r++){
                w[r]=0;
                for(int t=0; t<size; t++){
                    w[r]=w[r]+A[r][t]*v[t];
                }
            }
            s=s+"<p> "+w[0];
            double l=w[0]*w[0];        
            for ( j=1;j<size;j++){
                s=s+" "+w[j];
                l=l+w[j]*w[j];
            }
            s=s+"</p>\n";
            FaceCenter[i]=new JVPointDouble(w);
            System.out.println("Face center "+i+": "+FaceCenter[i].toString());
        }
       

        double w1, w2;
        JVPointDouble P1, P2, P;
        for (i=0;i<JVDualEdges.size();i++){
            JVEdge e=(JVEdge) JVDualEdges.elementAt(i);
            w1=e.getfromweight();
            w2=e.gettoweight();
            P1=(JVPointDouble) FaceCenter[e.from];
            P2=(JVPointDouble) FaceCenter[e.to];
            P=JVPointDouble.barycenter(P1, w1, P2, w2);
            s=s+"<p> "+P.coords[0];
            for ( j=1;j<size;j++){
                s=s+" "+P.coords[j];
            }
            s=s+"</p>\n";
        }

        s=s+"<thickness>10.0</thickness>\n";
        s=s+"<color type=\"rgb\">255 0 0</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 255</colorTag>\n";
        s=s+" </points>\n</pointSet>\n<faceSet edge=\"show\" face=\"show\">\n";
        s=s+"<faces num=\""+JVFaces.size()+"\">\n";
        
        
        for (i=0; i<JVPoints.size();i++){
            JVPoint p=(JVPoint) JVPoints.elementAt(i);
            
            s=s+"<f>";
            Vector FaceVertices=new Vector(JVFaces.size());
            Vector myFace=new Vector(10,1);
            
            for (j=0; j<JVFaces.size();j++){
                JVFace f=(JVFace) JVFaces.elementAt(j);
                int[] vert=f.Vertices;
                boolean isVertexOf=false;
                for (int k=0; k<vert.length;k++){
                    isVertexOf=isVertexOf||(vert[k]==i);
                }
                if (isVertexOf){
                    myFace.add(new Integer(j));
                }
            }
            
            int startCorner=0;
            for (int k=0; k<myFace.size();k++){
                Integer f1=(Integer) myFace.elementAt(k);
                int edgeNumber=0;
                for (int l=0; l<myFace.size();l++){
                    Integer f2=(Integer) myFace.elementAt(l);
                    boolean edgeFound;
                    if (k==l){
                        edgeFound=false;
                    }
                    else {
                        edgeFound=(JVDualEdges.indexOf(new JVEdge(f1.intValue(),f2.intValue()))!=-1);
                    }
                    if (edgeFound){
                        edgeNumber++;
                    }
                }
                //System.out.println("Face number: "+k+" Edge number: "+edgeNumber);
                if (edgeNumber==1){
                    startCorner=k;
                }
            }
            
            Vector myOrderedFace=new Vector(10,1);
            Integer f1=(Integer) myFace.elementAt(startCorner);
            myOrderedFace.add(f1);
            myFace.remove(startCorner);
            
            while (!myFace.isEmpty()){
                Integer f2=(Integer) myFace.elementAt(0);
                int k=0;
                boolean edgeFound=(JVDualEdges.indexOf(new JVEdge(f1.intValue(),f2.intValue()))!=-1);
                while ((!edgeFound)&(k+1<myFace.size())){
                    k=k+1;
                    f2=(Integer) myFace.elementAt(k);
                    edgeFound=(JVDualEdges.indexOf(new JVEdge(f1.intValue(),f2.intValue()))!=-1);
                }
                if (!edgeFound){
                    //System.out.println("No edge found from face "+f1.intValue());
                    k=0;
                    f2=(Integer) myFace.elementAt(k);
                }
                //System.out.println("Found edge : "+f1.intValue()+" to "+ f2.intValue());
                myOrderedFace.add(f2);
                myFace.remove(k);
                f1=f2;
            }
            
            for (int k=0; k<myOrderedFace.size(); k++){
                s=s+" "+((Integer) myOrderedFace.elementAt(k)).intValue();
            }
            s=s+"</f>\n";
        }
        
        
        s=s+"<color type=\"rgb\">255 255 255</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 255</colorTag>\n";
        s=s+"<creaseAngle>3.2</creaseAngle>\n";
        s=s+"</faces>\n";
        
        s=s+"<edges>";
        s=s+"<thickness>4.0</thickness>\n";
        s=s+"<color type=\"rgb\">0 0 200</color>\n";
        s=s+"<colorTag type=\"rgb\">255 0 0</colorTag>\n";
        s=s+"</edges>\n";
        s=s+"</faceSet>\n";
        
        s=s+"<center visible=\"hide\">\n";
        s=s+"<p>0 0 0</p>\n";
        s=s+"</center>/n";
        s=s+"</geometry>\n";
        s=s+"</geometries>\n";
        s=s+"</jvx-model>\n";
		
        return s;
    }
    
    public void setNoCoordTrsf(){
        A=Jama.Matrix.identity(size,size).getArray();
    }
    
    public Tracker (int arg, Mutable argMut, BMatrix M){
        size=arg;
        Type="Tracker";
        Mut=argMut;
        grvect=new GRvector(size,M);
        BMatrix C=new BMatrix(size,size);
        C.copyCartanCompanionFrom(M);
        Jama.Matrix JM=new Jama.Matrix(C.toDouble());
        //System.out.println("Matrix C:");
        JM.print(5,2);
        Jama.Matrix D=Jama.Matrix.identity(size,size);
        double cij, cji;
        for (int i=1; i<size; i++){
            cji=JM.getArray()[i-1][i];
            cij=JM.getArray()[i][i-1];
            if (Math.abs(cij)>0.01){
                D.getArray()[i][i]=cji*D.getArray()[i-1][i-1]/cij;
            }
            else {
                D.getArray()[i][i]=D.getArray()[i-1][i-1];
            }
        }
        JM=D.times(JM);
        //System.out.println("Symmetrized Matrix:");
        JM.print(5,2);
        
        EigenvalueDecomposition ED= new EigenvalueDecomposition(JM);
        D=ED.getD();
        for (int i=0; i<size; i++){
            double c=(D.getArray())[i][i];
            c=Math.sqrt(Math.abs(c));
            if (c<0.001){c=1;}
            D.getArray()[i][i]=c;
        }
        Jama.Matrix V=ED.getV();
        A=D.times(V.transpose()).getArray();
        
        //System.out.println("A times A transpose:");
        JM=new Jama.Matrix(A);
        JM.times(JM.transpose()).print(5,2);
        
        
        isnew=true;
        JVPoints=new Vector(10,1);
        JVFaces=new Vector(10,1);
        JVDualEdges=new Vector(10,1);
        BigInteger[] p;
        JVPoint jvp=null;
        int[] f=new int[size];
        JVFace jvf=null;
        for (int i=0; i<size; i++){
            f[i]=i;
            p=Mut.data(i);
            jvp=new JVPoint(p);
            JVPoints.add(jvp);
        }
        jvf=new JVFace(f);
        JVFaces.add(jvf);
        LastPoint=jvp;
        LastFace=jvf;
    }
    
    void mutate(Quiver Quiv, int k){
        Mut.mutate(Quiv, k);
        grvect.mutate(Quiv,k);
        boolean isgreen = grvect.ispositive(k);
        int targetface;
        LastPoint=new JVPoint(Mut.data(k));
        int ins=JVPoints.indexOf(LastPoint);
        //System.out.println("Mutate at "+k);
        //System.out.println(LastPoint+"Mutated Point index:"+ins);
        if (ins==-1){
            JVPoints.add(LastPoint);
            ins=JVPoints.size()-1;
        }
        int from=JVFaces.indexOf(LastFace);
        LastFace=new JVFace(LastFace.Vertices);
        LastFace.Vertices[k]=ins;
        ins=JVFaces.indexOf(LastFace);
        //System.out.println(LastFace+" Face index:"+ins);
        JVEdge e;
        if (ins==-1){
            JVFaces.add(LastFace);
            isnew=true;
            ins=JVFaces.size()-1;
        }
        else {
            isnew=false;
        }
        e=new JVEdge(from, ins);
        if (!isgreen){
            targetface=from;
        }
        else {
            targetface=ins;
        }
        e.settargetvertex(targetface);
        ins=JVDualEdges.indexOf(e);
        if (ins==-1){
            JVDualEdges.add(e);
            System.out.println("Added "+e.toString());
            System.out.println("Size: "+JVDualEdges.size());
        }
        
    }
    
    public boolean isnew(){
        return isnew;
    }
    
    public String toString(){
        return toString(0);
    }
    
    public String toString(int i){
       for (int j=0; j<JVPoints.size(); j++){
           System.out.println("Point "+j+":"+JVPoints.elementAt(j).toString());
       }
       for (int j=0; j<JVFaces.size(); j++){
           System.out.println("Face "+j+":"+JVFaces.elementAt(j).toString());
       }
       return "LP="+LastPoint.toString()+"\nLF="+LastFace.toString()+"\n";
    }
}


class Dispvector extends Mutable {
    BMatrix Disp;
    
    public String decompVector(BigInteger[] v){
        return Disp.decompVector(v);
    }
    
    public Dispvector(BMatrix A){
        size=A.nbrows;
        Type="disp-vectors";
        isMutable=false;
        Disp=null;
        Disp=new BMatrix(A.nbrows,A.nbcols);
        for (int i=0; i<A.nbrows; i++){
            for (int j=0; j<A.nbcols; j++){
                Disp.A[i][j]=new BigInteger(A.A[i][j].toString());
            }
        }
    }
    
    public int getIdNber(){
        return idnber;
    }
    
    public void setIdNber(int nb){
        idnber=nb;
    }
    
    public String getType(){
        return Type;
    }
    
    public void setType(String argType){
        Type=argType;
    }
    
    void mutate(Quiver Quiv, int k){
        System.out.println(Type+" cannot be mutated.");
    }
    
   
     public String toString(int i){
        return idnber+":"+Type+"["+(i+1)+"]:="+Disp.rowToString(i)+";\n";
    }
    
    
    public String toString(){
        int i;
        String s;
        
        s="";
        for (i=0; i<size; i++){
            s=s+toString(i);
        }
     return s;   
    }
}



class Hvector extends Mutable{
    BMatrix H; // contains h-vectors following DK
    Vector TrafoSeq; // for computing h-vectors following DK
    
    public Hvector(int narg, BMatrix B){
        Type="h-vectors";
        size=narg;
        H=BMatrix.IdentityMatrix(size);
        TrafoSeq=new Vector(10,1);
    }
    
    public BigInteger[] data(int k){
        return H.column(k);
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        SkewReflection mysr=new SkewReflection(k, M.column(k));
        TrafoSeq.add(mysr);
        //System.out.println("Added Transformation "+(TrafoSeq.size()-1)+":" + mysr.toString());
        BigInteger[] v=new BigInteger[M.nbrows];
        for (int i=0; i<M.nbrows; i++){
            if (i==k){
                v[i]=BigInteger.ONE;
            }
            else {
                v[i]=BigInteger.ZERO;
            }
        }
        //System.out.println("Initial vector:"+vecstring(v));

        for (int i=TrafoSeq.size()-1; i>=0; i--){
            mysr=(SkewReflection) TrafoSeq.elementAt(i);
            v=mysr.apply(v);
            //System.out.println("Vector after applying trafo"+i+":"+vecstring(v));
        }

        for (int i=0; i<M.nbrows; i++){
            H.A[i][k]=v[i];
        }
    }
    
    public String toString(int i){
        return "h["+(i+1)+"]:="+H.colToString(i)+";\n";
    }
}

class Weightvector extends Mutable{
    BMatrix W;
    BMatrix invCartan;
    int n,N; // n=nbpoints of original quiver, N=nber of translates, size=N*n
    int[] ht; //opposite of height function
    
    public Weightvector(BMatrix A, BMatrix C){
        size=A.nbrows;
        n=C.nbrows;
        N=size/n;
        Type="wt-vectors";
        W=new BMatrix(A.nbrows,A.nbcols);
        for (int i=0; i<A.nbrows; i++){
            for (int j=0; j<A.nbcols; j++){
                W.A[i][j]=new BigInteger(A.A[i][j].toString());
            }
        }
        ht=C.heightfunction();
        String s="";
        for (int i=0;i<ht.length;i++){
            s=s+" "+ht[i];
        }
        System.out.println(s);
        invCartan=new BMatrix(C.nbrows, C.nbcols);
        invCartan.copyCartanCompanionFrom(C);
        invCartan=invCartan.adjoint();
    }
    
    
    public String toString(int i){
        int j, vertex, generation;
        BigInteger exp;
        String s;
        s="";
        for (j=0;j<W.nbcols;j++){
             exp=W.A[i][j];
             int comp=exp.compareTo(BigInteger.ZERO);
             if (comp!=0){
                 generation=j/n;
                 vertex=j % n;
                 s=s+"["+(vertex+1)+","+(-2*generation-ht[vertex])+"]";
                 if (comp<0){
                     s=s+"^{"+exp+"}";
                 }
                 else {
                    if (exp.compareTo(BigInteger.ONE)>0){
                       s=s+"^{"+exp+"}";
                    }
                 }
             }
        }
        return "wt["+(i+1)+"]="+s+";\n";
        //g-weight="+weightvector(i).colToString(0)+"\n";
    }
    
    BigInteger weight(int row, int vertex){
        BigInteger sum=BigInteger.ZERO;
        for (int j=0;j<N;j++){
            sum=sum.add(W.A[row][vertex+j*n]);
        }
        return sum;
    }
    
    BigInteger weight(BigInteger[] Wt, int vertex){
        BigInteger sum=BigInteger.ZERO;
        for (int j=0; j<N;j++){
            sum=sum.add(Wt[vertex+j*n]);
        }
        return sum;
    }
    
    BMatrix weightvector(int row){
        BMatrix V=new BMatrix(n,1);
        for (int vertex=0; vertex<n;vertex++){
            V.A[vertex][0]=weight(row,vertex);
        }
        return V;
    }
    
    BMatrix weightvector(BigInteger[] Wt){
        BMatrix V=new BMatrix(n,1);
        for (int vertex=0;vertex<n;vertex++){
            V.A[vertex][0]=weight(Wt,vertex);
        }
        return V;
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        
        BigInteger[] inWt=new BigInteger[size];
        BigInteger[] outWt=new BigInteger[size];
        
        for (int i=0;i<size;i++){
            inWt[i]=BigInteger.ZERO;
            outWt[i]=BigInteger.ZERO;
        }
        
        for (int i=0;i<size;i++){
            if (M.A[k][i].signum()>0){
                    for (int j=0; j<size; j++){
                        outWt[j]=outWt[j].add(M.A[k][i].multiply(W.A[i][j]));
                    }
                }
                 if (M.A[i][k].signum()>0){
                    for (int j=0; j<size; j++){
                        inWt[j]=inWt[j].add(M.A[i][k].multiply(W.A[i][j]));
                    }    
                }
        }
        
        BMatrix inwt=weightvector(inWt);
        BMatrix outwt=weightvector(outWt);
        BMatrix diff=inwt.subtract(outwt);
        
        diff.leftmultiplyby(invCartan);
        
        if (diff.isPositive()){
            for (int i=0;i<size;i++){
                W.A[k][i]=inWt[i].subtract(W.A[k][i]);
            }
        }
        else {
            for (int i=0;i<size;i++){
                W.A[k][i]=outWt[i].subtract(W.A[k][i]);
            }
        }
    }
}


class Grepvector extends Mutable{
    BMatrix Grep;
    BigInteger[] h;
    BMatrix D;
    
    private boolean inouth(int k, BMatrix M){
        BigInteger s1, s2;
            s1=BigInteger.ZERO;
            s2=BigInteger.ZERO;
            
            for (int i=0; i<size; i++){
                if (M.A[k][i].signum()>0){
                    for (int j=0; j<size; j++){
                        s1=s1.add(M.A[k][i].multiply(Grep.A[i][j].multiply(h[j])));
                    }
                }
                 if (M.A[i][k].signum()>0){
                    for (int j=0; j<size; j++){
                        s2=s2.add(M.A[i][k].multiply(Grep.A[i][j].multiply(h[j])));
                    }    
                }
            }
            
            //System.out.println("Inouth: k="+k+" s1="+s1+" s2="+s2);
            return (s1.compareTo(s2)>=0);
    }
    
    private boolean inout(int k, BMatrix M){
        BigInteger s1, s2;
            s1=BigInteger.ZERO;
            s2=BigInteger.ZERO;
            
            for (int i=0; i<size; i++){
                if (M.A[k][i].signum()>0){
                    for (int j=0; j<size; j++){
                        s1=s1.add(M.A[k][i].multiply(D.A[i][j]));
                    }
                }
                 if (M.A[i][k].signum()>0){
                    for (int j=0; j<size; j++){
                        s2=s2.add(M.A[i][k].multiply(D.A[i][j]));
                    }    
                }
            }
            
            //System.out.println("s1="+s1+" s2="+s2);
            return (s1.compareTo(s2)>=0);
    }
    
    public String toString(int i){
        return "gr["+(i+1)+"]="+Grep.rowToString(i)+";\n";
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        
         if ((Grep!=null)&(h==null)){
            BigInteger s1, s2, s;
            s1=BigInteger.ZERO;
            s2=BigInteger.ZERO;
            for (int i=0; i<size; i++){
                if (M.A[k][i].signum()>0){
                    for (int j=0; j<size; j++){
                        s1=s1.add(M.A[k][i].multiply(Grep.A[i][j]));
                    }
                }
                if (M.A[i][k].signum()>0){
                    for (int j=0; j<size; j++){
                        s2=s2.add(M.A[i][k].multiply(Grep.A[i][j]));
                    }
                }
            }
            
            //System.out.println("s1="+s1+" s2="+s2);
            BigInteger[] Gknew=new BigInteger[size];
            if (s1.compareTo(s2)>0){
              for (int j=0; j<size; j++){
                  s=Grep.A[k][j].negate();
                  for (int i=0; i<size; i++){
                      if (M.A[k][i].signum()>0){
                            s=s.add(M.A[k][i].multiply(Grep.A[i][j]));    
                        }
                    }
                  Gknew[j]=new BigInteger(s.toString());
               }
             }
            
            if (s1.compareTo(s2)<=0){
                for (int j=0; j<size; j++){
                  s=Grep.A[k][j].negate();
                  for (int i=0; i<size; i++){
                      if (M.A[k][i].signum()<0){
                            s=s.add(M.A[k][i].negate().multiply(Grep.A[i][j]));    
                        }
                    }
                  Gknew[j]=new BigInteger(s.toString());
               }
            }
            for (int j=0; j<size; j++){
                    Grep.A[k][j]=Gknew[j];
                }
         }
        
         if ((Grep!=null)&(h!=null)){
            
            BigInteger s1, s2, s;
            s1=BigInteger.ZERO;
            s2=BigInteger.ZERO;
            
            for (int i=0; i<size; i++){
                if (M.A[k][i].signum()>0){
                    for (int j=0; j<size; j++){
                        s1=s1.add(M.A[k][i].multiply(Grep.A[i][j].multiply(h[j])));
                    }
                }
                 if (M.A[i][k].signum()>0){
                    for (int j=0; j<size; j++){
                        s2=s2.add(M.A[i][k].multiply(Grep.A[i][j].multiply(h[j])));
                    }    
                }
            }
            
            //System.out.println("s1="+s1+" s2="+s2);
            BigInteger[] Gknew=new BigInteger[size];
            if (s1.compareTo(s2)>0){
              for (int j=0; j<size; j++){
                  s=Grep.A[k][j].negate();
                  for (int i=0; i<size; i++){
                      if (M.A[k][i].signum()>0){
                            s=s.add(M.A[k][i].multiply(Grep.A[i][j]));    
                        }
                    }
                  Gknew[j]=new BigInteger(s.toString());
               }
             }
            
            if (s1.compareTo(s2)<=0){
                for (int j=0; j<size; j++){
                  s=Grep.A[k][j].negate();
                  for (int i=0; i<size; i++){
                      if (M.A[k][i].signum()<0){
                            s=s.add(M.A[k][i].negate().multiply(Grep.A[i][j]));    
                        }
                    }
                  Gknew[j]=new BigInteger(s.toString());
               }
            }
            
            for (int j=0; j<size; j++){
                    Grep.A[k][j]=Gknew[j];
            }
            
            M.mutate(k);
            
            for (int j=0; j<size; j++){
                if (!Quiv.P[j].frozen){
                 if (inouth(j, M)){
                    Quiv.P[j].setColor(Color.GREEN);
                    }    
                 else {
                     Quiv.P[j].setColor(Color.RED);
                 }
                }
                
            }
            
            }
    }
    
    public Grepvector(BMatrix A){
        size=A.nbrows;
        Type="gr-vectors";
        Grep=null;
        Grep=new BMatrix(A.nbrows,A.nbcols);
        for (int i=0; i<A.nbrows; i++){
            for (int j=0; j<A.nbcols; j++){
                Grep.A[i][j]=new BigInteger(A.A[i][j].toString());
            }
        }
        h=null;
    }
    
    public Grepvector(BMatrix A, BigInteger[] harg){
        size=A.nbrows;
        Type="gr-vectors";
        Grep=null;
        Grep=A;
        h=null;
        h=harg;
    }
}


class Fvector extends Mutable{
    Fpolynomial fp;
    
    public Fvector(int narg){
        size=narg;
        Type="f-vectors";
        fp=new Fpolynomial(narg);
    }
    
    public String toString(int i){
        int[] v=fp.ExponentVector(i);
        String s="f["+(i+1)+"]:=["+v[0];
        for (int j=1;j<size;j++){
            s=s+","+v[j];
        }
        s=s+"];\n";
        return s;
    }
    
    void mutate(Quiver Quiv, int k){
        fp.mutate(Quiv, k);
    }
}

class Guovector extends Mutable{
    Gvector gv;
    BMatrix Btimesy;
    BMatrix B_loc;
    
    public Guovector(int narg, BMatrix B, BigInteger[] argy){
        B_loc=new BMatrix(B);
        size=narg;
        gv=new Gvector(narg, B);
    }
    
    public String toString(int r){
        BMatrix T=new BMatrix(B_loc);
        T.transpose();
        try {
        T.invert();
        }
        catch (Exception e){
	    System.out.println(e.getMessage());
        }
        System.out.print("B-matrix:\n"+B_loc);
        System.out.print("Inverse:\n"+T);
        System.out.println("g-matrix:\n"+gv.G);
        
        T.multiplyby(gv.G);
        
        String s="";
        s="guo:\n"+T;
        //s="guo["+(r+1)+"]=[";
       // for (int j=0;j<size;j++){
        //    s=s+T.A[j][r];
         //   if (j<size-1){s=s+",";}
        //    if (j==size-1){s=s+"];\n";}
        //}
        return s;
    }
    
    void mutate(Quiver Quiv, int k){
        gv.mutate(Quiv,k);
        B_loc.mutate(k);
    }
}

class Gvector extends Mutable{
    BMatrix G;
    BMatrix C;
    BMatrix B0;
    
    public BigInteger[] data(int k){
        return G.column(k);
    }
    
    public Gvector(int narg, BMatrix B){
        size=narg;
        Type="g-vectors";
        C=BMatrix.IdentityMatrix(size);
        B0=new BMatrix(B);
        G=BMatrix.IdentityMatrix(size);
    }

    public void permuteVertices(int[] perm){
        G.permuteCols(perm);
        C.permuteCols(perm);
    }
    
    public String toString(int i){
        return "g["+(i+1)+"]:="+G.colToString(i)+";\n";
        //return "g["+(i+1)+"]:="+G.colToVecString(i)+";\n";
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        for (int i=0;i<size;i++){
                BigInteger s=BigInteger.ZERO;
                for (int j=0; j<size; j++){
                    BigInteger ma=Utils.max(M.A[j][k], BigInteger.ZERO);
                    s=s.add(ma.multiply(G.A[i][j]));
                    ma=Utils.max(C.A[j][k], BigInteger.ZERO);
                    s=s.subtract(ma.multiply(B0.A[i][j]));
                }
                G.A[i][k]=s.subtract(G.A[i][k]);
            }
        BMatrix Cp=new BMatrix(size,size);
            for (int i=0;i<size;i++){
                for (int j=0; j<size;j++){
                    if (j==k){
                        Cp.A[i][j]=C.A[i][j].negate();
                    }
                    else {
                      Cp.A[i][j]=C.A[i][j].add(BMatrix.strangeprod(C.A[i][k],M.A[k][j]));
                    }
                }
            }
            C=null;
            C=Cp;
            BMatrix D=new BMatrix(G);
            D.transpose();
            D.multiplyby(C);
            //D.transpose();
            System.out.println(D);
    }
}

class Glincomb extends Gvector{
    
    public Glincomb(int narg, BMatrix B){
        super(narg,B);
        Type="g-lincombs";
    }
        
     public String toString(int i){
        //return "g["+(i+1)+"]:="+G.colToString(i)+";\n";
        return "g["+(i+1)+"]:="+G.colToVecString(i)+";\n";
    }   

}

class Gmatrix extends Gvector{
    
    public Gmatrix(int narg, BMatrix B){
        super(narg,B);
        Type="g-matrix";
    }
        
     public String toString(int i){
        //return "g["+(i+1)+"]:="+G.colToString(i)+";\n";
        //return "g["+(i+1)+"]:="+G.colToVecString(i)+";\n";
         return "Mutate at "+(i+1)+" :\n"+G.toString();
    }   

}


class GRvector extends Mutable{
    BMatrix G;
    boolean[] hasChanged;
    
    public BigInteger[] data(int k){
        return G.column(k);
    }
    
    public GRvector(int narg, BMatrix B){
        size=narg;
        Type="gr-vectors";
        G=BMatrix.IdentityMatrix(size);
        hasChanged=new boolean[size];
        for (int i=0;i<size;i++){
            hasChanged[i]=true;
        }
    }
    
    public boolean ispositive(int i){
        boolean ans=true;
        for (int j=0;j<size;j++){
            ans=ans && (G.A[i][j].compareTo(BigInteger.ZERO)>=0);
        }
        return ans;
    }
    
    public String toString(int i){
        //return "g["+(i+1)+"]:="+G.colToString(i)+";\n";
        String s="";
        for (int j=0;j<size;j++){
            if (true){
                s=s+"gr["+(j+1)+"]:="+G.rowToString(j)+";\n";
            }
        }
        return s;
    }

    public boolean isgreen(int j){
        return ispositive(j);
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        BMatrix Gnew=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        for (int j=0;j<size;j++){
            hasChanged[j]=false;
            for (int i=0;i<size;i++){
                if (i==k){
                    Gnew.A[i][j]=G.A[i][j].negate();
                }
                else {
                    Gnew.A[i][j]=G.A[i][j].add(BMatrix.strangeprod(M.A[i][k],G.A[k][j]));
                }
                hasChanged[j]=hasChanged[j]||(Gnew.A[i][j].compareTo(G.A[i][j])!=0);
            }
        }
        G=Gnew;
        for (int j=0; j<size; j++){
                if (!Quiv.P[j].frozen){
                 if (ispositive(j)){
                    Quiv.P[j].setColor(Color.GREEN);
                    }
                 else {
                     Quiv.P[j].setColor(Color.RED);
                 }
                }

            }
    }
}



class Fpolynomial extends Mutable{
    Ring RF;
    RingElt[] F;
    RingElt lastQuot;
    BMatrix C;
    
    public int[] ExponentVector(int i){
        return Utils.ExponentVector(F[i],RF,size);
    }
    
    public String toString(int i){
        //return "f["+(i+1)+"]:="+F[i].toString()+";\nlq:="+lastQuot.toString()+";\n";
        return "f["+(i+1)+"]:="+F[i].toString()+";\n";
    }
    
    public Fpolynomial(int narg, Ring RFarg){
        size=narg;
        int i;
        String s;
        
        Type="F-polynomials";
        //System.out.println("Entering Cluster.activateFPols");
        
        F=null;
        RF=RFarg;
        
        F=new RingElt[size];
        for (i=0; i<size; i++){
            F[i]=RF.one();
            //System.out.println(X[i]);
        } 
        C=BMatrix.IdentityMatrix(size);
        lastQuot=RF.one();
    }
    
    public Fpolynomial(int narg){
        size=narg;
        int i;
        String s;
        
        Type="F-polynomials";
        //System.out.println("Entering Cluster.activateFPols");
        
        F=null;
        RF=null;
        
        s="x1";
        for (i=2;i<=size;i++){
            s=s+",x"+i;
        }
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        
        
        //System.out.println(s);
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
        
        /*
        XF=new RingElt[size];
        for (i=0; i<size; i++){
            XF[i]=RF.map("x"+(i+1));
            //System.out.println(X[i]);
        }
        
        YF=new BigInteger[size][size];
        for (i=0; i<size; i++){
            for (int j=0; j<size; j++){
               YF[i][j]=BigInteger.ZERO;
               if (i==j){YF[i][j]=BigInteger.ONE;}
            }
            //System.out.println(X[i]);
        } 
         *
         */
        
        F=new RingElt[size];
        for (i=0; i<size; i++){
            F[i]=RF.one();
            //System.out.println(X[i]);
        } 
        C=BMatrix.IdentityMatrix(size);
        lastQuot=RF.one();
        
    }
    
    

    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        RingElt r1=RF.one();
        RingElt r2=RF.one();
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=RF.mult(r1, RF.pow(F[i], c));
              }
             else{
                 //System.out.println("r2:"+r2);
                 //System.out.println(" RF:"+RF);
                 //System.out.println(" c:"+c);
                 r2=RF.mult(r2, RF.pow(F[i], c.negate()));
              }
           }
       for (int i=0;i<C.nbrows;i++){
             BigInteger c=C.A[i][k];
             if (c.signum()>0){
                   r1=RF.mult(r1, RF.pow(RF.map("y"+(i+1)), c));
              }
             else{
                 //System.out.println("r2:"+r2);
                 //System.out.println(" RF:"+RF);
                 //System.out.println(" c:"+c);
                 r2=RF.mult(r2, RF.pow(RF.map("y"+(i+1)), c.negate()));
              }
           }
       RingElt r=RF.add(r1,r2);
       //System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+F[k]);
       r=RF.div(r,F[k]);
       //System.out.println("Division successful.");
       //System.out.println("Variable " + k + " : " + X[k]);
       lastQuot=F[k];
       F[k]=null;
       F[k]=r;
       lastQuot=RF.div(F[k],lastQuot);
       
        BMatrix Cp=new BMatrix(size,size);
            for (int i=0;i<size;i++){
                for (int j=0; j<size;j++){
                    if (j==k){
                        Cp.A[i][j]=C.A[i][j].negate();
                    }
                    else {
                      Cp.A[i][j]=C.A[i][j].add(BMatrix.strangeprod(C.A[i][k],M.A[k][j]));
                    }
                }
            }
            C=null;
            C=Cp;
            //System.out.println(new Matrix(C));
        
}
    
}
    
class Fppolynomial extends Mutable{
    Ring RF;
    RingElt[] F;
    BMatrix C;
    
    public int[] ExponentVector(int i){
        return Utils.ExponentVector(F[i],RF,size);
    }
    
    
    public String toString(int i){
        
        RingElt G;
        StringBuffer temp=new StringBuffer(F[i].toString());
        System.out.println(temp);
        for (int j=0; j<size;j++){
            System.out.println(temp);
            Utils.replaceAll(temp,"y"+(j+1), "(1/z"+(j+1)+")");
        }
        Utils.replaceAll(temp, "z", "y");
        G=RF.map(temp.toString());
        
        return "f'["+(i+1)+"]:="+G.toString()+";\n";
    }
    
    public Fppolynomial(int narg){
        size=narg;
        int i;
        String s;
        
        Type="F'-polynomials";
        //System.out.println("Entering Cluster.activateFPols");
        
        F=null;
        RF=null;
        
        s="x1";
        for (i=2;i<=size;i++){
            s=s+",x"+i;
        }
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        
        
        //System.out.println(s);
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
        
        /*
        XF=new RingElt[size];
        for (i=0; i<size; i++){
            XF[i]=RF.map("x"+(i+1));
            //System.out.println(X[i]);
        }
        
        YF=new BigInteger[size][size];
        for (i=0; i<size; i++){
            for (int j=0; j<size; j++){
               YF[i][j]=BigInteger.ZERO;
               if (i==j){YF[i][j]=BigInteger.ONE;}
            }
            //System.out.println(X[i]);
        } 
         *
         */
        
        F=new RingElt[size];
        for (i=0; i<size; i++){
            F[i]=RF.one();
            //System.out.println(X[i]);
        } 
        C=BMatrix.IdentityMatrix(size);
        
    }
    
    

    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        M.negate();
        RingElt r1=RF.one();
        RingElt r2=RF.one();
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=RF.mult(r1, RF.pow(F[i], c));
              }
             else{
                 //System.out.println("r2:"+r2);
                 //System.out.println(" RF:"+RF);
                 //System.out.println(" c:"+c);
                 r2=RF.mult(r2, RF.pow(F[i], c.negate()));
              }
           }
       for (int i=0;i<C.nbrows;i++){
             BigInteger c=C.A[i][k];
             if (c.signum()>0){
                   r1=RF.mult(r1, RF.pow(RF.map("y"+(i+1)), c));
              }
             else{
                 //System.out.println("r2:"+r2);
                 //System.out.println(" RF:"+RF);
                 //System.out.println(" c:"+c);
                 r2=RF.mult(r2, RF.pow(RF.map("y"+(i+1)), c.negate()));
              }
           }
       RingElt r=RF.add(r1,r2);
       System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+F[k]);
       r=RF.div(r,F[k]);
       System.out.println("Division successful.");
       //System.out.println("Variable " + k + " : " + X[k]);
       F[k]=null;
       F[k]=r;
       
        BMatrix Cp=new BMatrix(size,size);
            for (int i=0;i<size;i++){
                for (int j=0; j<size;j++){
                    if (j==k){
                        Cp.A[i][j]=C.A[i][j].negate();
                    }
                    else {
                      Cp.A[i][j]=C.A[i][j].add(BMatrix.strangeprod(C.A[i][k],M.A[k][j]));
                    }
                }
            }
            C=null;
            C=Cp;
            //System.out.println(new Matrix(C));
        
}
    
}   

class QXFvariable extends Mutable{
    Ring RF;
    XYBvariable xb;
    Fpolynomial fpol;


    
    public String toString(int i){
        
        RingElt G;
        G=RF.div(xb.XF[i], fpol.F[i]);
        return "qxf["+(i+1)+"]:="+G.toString()+";\n";
    }
    
    public QXFvariable(int narg){
        size=narg;
        int i;
        String s;
        
        Type="QXF-variables";
        //System.out.println("Entering Cluster.activateFPols");
        
        RF=null;
        
        s="x1";
        for (i=2;i<=size;i++){
            s=s+",x"+i;
        }
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
        
        xb=new XYBvariable(narg, RF);
        fpol=new Fpolynomial(narg, RF);
        
    }
    
    

    void mutate(Quiver Quiv, int k){
        xb.mutate(Quiv,k);
        fpol.mutate(Quiv,k);
}
    
}   

class Dvector extends Mutable{
    BMatrix D;
    
    public BigInteger[] data(int k){
        return D.row(k);
    }
    
    public Dvector(int narg){
        size=narg;
        Type="d-vectors";
        D=null;
        D=new BMatrix(size,size);
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                if(i==j){D.A[i][j]=new BigInteger("-1");} else {D.A[i][j]=new BigInteger("0");}
            }
          }
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        //System.out.println(showDim());
        for (int j=0;j<D.nbrows;j++){
            BigInteger s1=new BigInteger("0");
            BigInteger s2=new BigInteger("0");
            for (int i=0; i<D.nbrows; i++){
                int sig=M.A[i][k].signum();
                if (sig>0){
                    s1=s1.add(D.A[i][j].multiply(M.A[i][k]));
                }
                if (sig<0){
                    s2=s2.subtract(D.A[i][j].multiply(M.A[i][k]));
                }
            }
            D.A[k][j]=Utils.max(s1,s2).subtract(D.A[k][j]);
            //System.out.println(showDim());
            s1=null; s2=null;
        }
    }
    
    public String toString(int i){
        return "d["+(i+1)+"]:="+D.rowToString(i)+";\n";
    }
}

class QXvariable extends Mutable{
    Ring R;
    RingElt V;
    //BMatrix Lamlocal;
    Polynomial[] X;
    //String memory;
    
    public Polynomial Mpos(BMatrix Lambda, BigInteger[] a){
       BigInteger s=BigInteger.ZERO;
       for (int i=0; i<size; i++){
           for (int j=i+1; j<size; j++){
               s=s.add(Lambda.A[i][j].multiply(a[i].multiply(a[j])));
           }
       }
       s=s.negate();
       RingElt r=R.pow(V,s);
       Polynomial P=new Polynomial(r);
       for (int i=0; i<size; i++){
           P=P.mult(X[i].pow(a[i]));
       }
       return P;
    }
    
    public Polynomial M(BMatrix Lambda, BigInteger[] a){
        BigInteger[] ap=new BigInteger[size];
        BigInteger[] an=new BigInteger[size];
        int s;
        for (int i=0; i<size; i++){
            ap[i]=BigInteger.ZERO;
            an[i]=BigInteger.ZERO;
            s=a[i].compareTo(BigInteger.ZERO);
            if (s>0){ap[i]=a[i];}
            if (s<0){an[i]=a[i].negate();}
        }
        //System.out.println("M: "+Utils.toString(ap)+ ", " + Utils.toString(an));
        RingElt c=R.pow(V,Lambda.bilform(an,ap));
        //System.out.println("Factor: "+c);
        Polynomial P=new Polynomial(c);
        P=P.mult(Mpos(Lambda, ap));
        //System.out.println("Pos. factor: "+P);
        Polynomial Q=Mpos(Lambda,an);
        //System.out.println("Neg. factor: "+Q);
        Polynomial R=P.div(Q);
        //System.out.println("Quotient: "+R);
        return R;
    }
    
    public QXvariable(int narg, BMatrix argLamlocal){
        int i;
        String s;
        
        size=narg;
        Type="QX-variables";
        isMutable=true;
        //memory="";
        
        BMatrix Lamlocal=new BMatrix(argLamlocal);
        Monomial.initialize(Lamlocal);
        System.out.println("q-commutation Matrix Lambda:\n"+Lamlocal);
        
        X=null;
        R=null;
        
        R=new QuotientField(new PolynomialRing(Ring.Z,"v"));
        V=R.map("v");
        
        X=new Polynomial[size];
        for (i=0; i<size; i++){
            X[i]=new Polynomial(new Monomial(i));
            System.out.println(X[i]);
        } 
    }
    
    public String toString(int i){
        return "qx["+(i+1)+"]:="+X[i].toString()+";\n";
        //return "Lambda:\n"+Lamlocal.toString();
    }
    
    public void testLambda(BMatrix Lamlocal){
        Polynomial P1;
        Polynomial P2;
        System.out.println("Matrix Lambda:\n"+Lamlocal);
        for (int i=0;i<size;i++){
            for (int j=0; j<size; j++){
                P1=X[i].mult(X[j]);
                P2=X[j].mult(X[i]).mult(R.pow(V,Lamlocal.A[i][j].multiply(BigInteger.valueOf(2))));
                System.out.println("Commutator ("+i+", "+j+"): "+P1.subtract(P2));
            }
        }
    }
    public static BMatrix LambdaPrincipal(BMatrix C){
        
        BMatrix B=new BMatrix(C);
        
        if (B.nbrows==2){
            B.A[0][1]=BigInteger.valueOf(B.A[0][1].signum());
            B.A[1][0]=B.A[0][1].negate();
            B.A[0][0]=BigInteger.ZERO;
            B.A[1][1]=BigInteger.ZERO;
            return B;
        }
        
        if (!B.isAntisymmetric()){
            return null;
        }
        
        try{
            B.invert();
        }
        catch (Exception e){
	    System.out.println(e.getMessage());
	}
        B.transpose();
        return B;
        
        /*
        int   n=Math.round(C.nbrows/2);
        System.out.println("n="+n);
        System.out.println(C);
        B=C.extract(0,n,0,n);
        System.out.println("B="+B);
        BMatrix L=new BMatrix(2*B.nbrows,2*B.nbcols);
        L.makeZero();
        int i,j;

        for (i=n; i<2*n; i++){
            L.A[i][i-n]=BigInteger.ONE;
            L.A[i-n][i]=BigInteger.ONE.negate();
        }
        for (i=n; i<2*n; i++){
            for (j=n; j<2*n;j++){
                L.A[i][j]=new BigInteger(B.A[j-n][i-n].toString());
                }
            }
         
    return L;
    */
}
    
    void mutate(Quiver Quiv, int k){
        if (!isMutable){return;}
        
        BMatrix M=new BMatrix(Quiv.M);
        BMatrix Lambda=new BMatrix(Quiv.Lambda);
        
        //System.out.println("Mutating at "+k);
        testLambda(Lambda);
        
        int s;
        BigInteger[] a=new BigInteger[size];
        BigInteger[] b=new BigInteger[size];
        BigInteger[] ek=new BigInteger[size];
        for (int i=0; i<size; i++){
            s=M.A[i][k].compareTo(BigInteger.ZERO);
            a[i]=BigInteger.ZERO;
            b[i]=BigInteger.ZERO;
            ek[i]=BigInteger.ZERO;
            if (s>0){a[i]=M.A[i][k];}
            if (s<0){b[i]=M.A[i][k].negate();}
            if (i==k){ek[i]=BigInteger.ONE;}
        }
        RingElt f1=R.pow(V,Lambda.bilform(a,ek));
        RingElt f2=R.pow(V,Lambda.bilform(b,ek));
        //System.out.println("Exponents: "+Utils.toString(a)+"; "+Utils.toString(b));
        //System.out.println("Factors: "+f1+", "+f2);
        Polynomial M1=M(Lambda,a).mult(f1);
        Polynomial M2=M(Lambda,b).mult(f2);
        Polynomial P=M1.add(M2);
        X[k]=P.div(X[k]);
        
        /*
        System.out.println("Mutated Lambda: \n"+Lamlocal);
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                System.out.println("Lambda("+i+","+j+")="+Lamlocal.A[i][j]);
                System.out.println("X[i]*X[j]: "+X[i].mult(X[j]));
                System.out.println("X[j]*X[i]: "+X[j].mult(X[i]));
            }
        }
        */
        
    }
}

class Xvariable extends Mutable{
    Ring R;
    RingElt[] X;
    

    
    public Xvariable(RingElt[] argX){
        size=argX.length;
        Type="X-variables";
        isMutable=false;
         
        X=argX;
        R=X[0].getRing();
        System.out.println("Ring R in X-variable: "+R);
    }
    
    public Xvariable(int narg){
        int i;
        String s;
        
        size=narg;
        Type="X-variables";
        isMutable=true;
      
        X=null;
        R=null;
        s="";
        for (i=1;i<=size;i++){
            s=s+",x"+i;
        }
        
        //System.out.println(s);
        R=new QuotientField(new PolynomialRing(Ring.Z, s));
        X=new RingElt[size];
        for (i=0; i<size; i++){
            X[i]=R.map("x"+(i+1));
            //System.out.println(X[i]);
        } 
    }
    
    public String toString(int i){
        return "x["+(i+1)+"]:="+X[i].toString()+";\n";
    }
    
    void mutate(Quiver Quiv, int k){
        if (!isMutable){return;}
        
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        RingElt r1=R.one();
        RingElt r2=R.one();
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=R.mult(r1, R.pow(X[i], c));
              }
             else{
                  r2=R.mult(r2, R.pow(X[i], c.multiply(new BigInteger("-1"))));
              }
          }
           RingElt r=R.add(r1,r2);
           System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+X[k]);
           r=R.div(r,X[k]);
           System.out.println("Division successful.");
           //System.out.println("Variable " + k + " : " + X[k]);
           X[k]=null;
           X[k]=r;
           //System.out.println("Variable " + k +" : "+X[k]);
        
    }
}

class NumXvariable extends Mutable{
    Ring R;
    RingElt[] X;
    String[] cumul;
    
    public NumXvariable(RingElt[] argX){
        size=argX.length;
        Type="num. X-variables";
        isMutable=false;
         
        X=argX;
        R=X[0].getRing();
       
    }
    
    public NumXvariable(String initialValues){
        int i;
        String s;
        String patternstr=" ";
        String[] fields=initialValues.split(patternstr);
        
        size=fields.length;
        Type="num. X-variables";
        isMutable=true;
        
        X=null;
        R=null;
        cumul=null;
        s="";
        for (i=1;i<=size;i++){
            s=s+",x"+i;
        }
        //System.out.println(s);
        cumul=new String[size];
        R=new QuotientField(new PolynomialRing(Ring.Z, s));
        X=new RingElt[size];
        for (i=0; i<size; i++){
            X[i]=R.map(fields[i]);
            cumul[i]=X[i].toString();
            //System.out.println(X[i]);
        } 
    }
    
    public NumXvariable(int narg){
        int i;
        String s;
        
        size=narg;
        Type="num. X-variables";
        isMutable=true;
        
        
        X=null;
        R=null;
        cumul=null;
        s="";
        for (i=1;i<=size;i++){
            s=s+",x"+i;
        }
        //System.out.println(s);
        cumul=new String[size];
        R=new QuotientField(new PolynomialRing(Ring.Z, s));
        X=new RingElt[size];
        for (i=0; i<size; i++){
            X[i]=R.one();
            cumul[i]=X[i].toString();
            //System.out.println(X[i]);
        } 
        
    }
    
    public String toString(int i){
        String s="nx["+(i+1)+"]:=["+cumul[i]+"];\n";
        BigInteger[] R=BMatrix.FindRecurrence(cumul[i]);
        
        if (R!=null){
            s=s+"Rec.: "+Utils.toString(R)+"\n";
        }
        return s;
        //return "x["+(i+1)+"]:="+X[i].toString()+";\n";
    }
    
    void mutate(Quiver Quiv, int k){
        if (!isMutable){return;}
        
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        RingElt r1=R.one();
        RingElt r2=R.one();
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=R.mult(r1, R.pow(X[i], c));
              }
             else{
                  r2=R.mult(r2, R.pow(X[i], c.multiply(new BigInteger("-1"))));
              }
          }
           RingElt r=R.add(r1,r2);
           System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+X[k]);
           r=R.div(r,X[k]);
           System.out.println("Division successful.");
           //System.out.println("Variable " + k + " : " + X[k]);
           X[k]=null;
           X[k]=r;
           cumul[k]=cumul[k]+","+X[k];
           //System.out.println("Variable " + k +" : "+X[k]);
        
    }
}


class NumXtvariable extends Mutable{
    BigInteger[] X;
    
    public NumXtvariable(BigInteger[] argX){
        size=argX.length;
        Type="num. Xt-variables";
        isMutable=false;
         
        X=argX;
    }
    
    public NumXtvariable(String initialValues){
        int i;
        String s;
        String patternstr=" ";
        String[] fields=initialValues.split(patternstr);
        
        size=fields.length;
        Type="num. X-variables";
        isMutable=true;
        
        X=new BigInteger[size];
        for (i=0; i<size; i++){
            X[i]=new BigInteger(fields[i]);
            //System.out.println(X[i]);
        } 
    }
    
    public NumXtvariable(int narg){
        int i;
        String s;
        
        size=narg;
        Type="num. Xt-variables";
        isMutable=true;
        
        X=null;
        X=new BigInteger[size];
        for (i=0; i<size; i++){
            X[i]=BigInteger.ONE;
            //System.out.println(X[i]);
        } 
        
    }
    
    public String toString(int i){
        String s="nxt["+(i+1)+"]:="+X[i]+";\n";
        return s;
        //return "x["+(i+1)+"]:="+X[i].toString()+";\n";
    }
    
    void mutate(Quiver Quiv, int k){
        if (!isMutable){return;}
        
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        BigInteger r1=BigInteger.ZERO;
        BigInteger r2=BigInteger.ZERO;
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=r1.add(X[i].multiply(c));
              }
             else{
                  r2=r2.add(X[i].multiply(c.negate()));
              }
          }
         BigInteger r=r1.max(r2);
           //System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+X[k]);
           r=r.subtract(X[k]);
           //System.out.println("Division successful.");
           //System.out.println("Variable " + k + " : " + X[k]);
           X[k]=r;
           //cumul[k]=cumul[k]+","+X[k];
           //System.out.println("Variable " + k +" : "+X[k]);
        
    }
}

class Yvariable extends Mutable{
    boolean[] hasChanged;
    Ring S;
    RingElt[] Y;
    
    public Yvariable(RingElt[] argY){
        size=argY.length;
        Type="Y-variables";
        isMutable=true;
         
        Y=argY;
        S=Y[0].getRing();
        
        hasChanged=new boolean[size];
        for (int i=0;i<size;i++){
            hasChanged[i]=true;
        }
        
        System.out.println("Ring S in Y-variable: "+S);
    }
    
    public Yvariable(int narg){
        int i;
        String s;
        
        size=narg;
        Type="Y-variables";
        
        hasChanged=new boolean[size];
        for (i=0;i<size;i++){
            hasChanged[i]=true;
        }
        Y=null;
        S=null;
        s="";
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        
        //System.out.println(s);
        S=new QuotientField(new PolynomialRing(Ring.Z, s));
        Y=new RingElt[size];
        for (i=0; i<size; i++){
            Y[i]=S.map("y"+(i+1));
            //System.out.println(X[i]);
        } 
    }

    
    
    public String toString(){
        String s="";
        for (int j=0;j<size;j++){
            s=s+"y["+(j+1)+"]:="+Y[j].toString()+";\n";
        }
        //System.out.println("s="+s);
        return s;
    }
    
    public String toString(int i){
        String s="";
        for (int j=0;j<size;j++){
            if (!hasChanged[j]){
                continue;
            }
            s=s+"y["+(j+1)+"]:="+Y[j].toString()+";\n";
        }
        //System.out.println("s="+s);
        return s;
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        RingElt r1=S.one();
            RingElt r2;
            RingElt oldel;
            for (int i=0;i<M.nbrows;i++){
                if (i==k){continue;}
                 BigInteger c=M.A[k][i];
                 if (c.signum()<0){
                      r2=S.add(r1,Y[k]);
                  }
                 else{
                      r2=S.add(r1,S.div(r1,Y[k]));
                  }
                 oldel=Y[i];
                 Y[i]=S.mult(Y[i], S.pow(r2,c.multiply(new BigInteger("-1"))));
                 hasChanged[i]= (!oldel.equals(Y[i]));
                 //System.out.println("Variable Y " + i +" : "+Y[i]);
            }
            oldel=Y[k];
            Y[k]=S.div(r1,Y[k]);
            hasChanged[k]=(!oldel.equals(Y[k]));
    }
}

class Ytropvariable extends Mutable{
    boolean[] hasChanged;
     BigInteger[][] YT;
     Ring RF;
     
    public Ytropvariable(int narg){
        int i;
        String s;
        
        size=narg;
        Type="Yt-variables";
        hasChanged=new boolean[size];
        for (i=0;i<size;i++){
            hasChanged[i]=true;
        }
        
        YT=new BigInteger[size][size];
        for (i=0; i<size; i++){
            for (int j=0; j<size; j++){
               YT[i][j]=BigInteger.ZERO;
               if (i==j){YT[i][j]=BigInteger.ONE;}
            }
            //System.out.println(X[i]);
        }
        
        s="y1";
        for (i=2;i<=size;i++){
            s=s+",y"+i;
        }
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
    }

    public String toString(){
        String s="";
        for (int j=0;j<size;j++){
            s=s+"Yt["+(j+1)+"]:="+Utils.yexp(RF,YT[j]).toString()+";\n";
        }
        //System.out.println("s="+s);
        return s;
    }
    
    public String toString(int i){
        String s="";
        for (int j=0;j<size;j++){
            if (!hasChanged[j]){
                continue;
            }
            s=s+"Yt["+(j+1)+"]:="+Utils.yexp(RF,YT[j]).toString()+";\n";
        }
        //System.out.println("s="+s);
        return s;
    }
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
            BigInteger[][] YFnew=new BigInteger[size][size];
            YFnew[k]=Utils.scalmult(BigInteger.ONE.negate(),YT[k]);
            hasChanged[k]=true;
            for (int j=0; j<size; j++){
                if (j!=k){
                    BigInteger[] big=new BigInteger[size];
                    big=Utils.minzero(YT[k]);
                    big=Utils.scalmult(M.A[k][j].negate(),big);
                    big=Utils.sum(big,YT[j]);
                    BigInteger c=Utils.max(BigInteger.ZERO,M.A[k][j]);
                    YFnew[j]=Utils.sum(big,Utils.scalmult(c,YT[k]));
                    boolean change=false;
                    for (int l=0;l<size;l++){
                        change=change || (!YFnew[j][l].equals(YT[j][l]));
                    }
                    hasChanged[j]=change;
                }
            }
            YT=null;
            YT=YFnew;
    }

}

class XYvariable extends Mutable{
    Ring RF;
    RingElt[] XF;
    BigInteger[][] YF;
    boolean[] hasChanged;
    
    void mutate(Quiver Quiv, int k){
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        if (XF!=null){
        RingElt r1=RF.one();
        RingElt r2=RF.one();
        for (int i=0;i<M.nbrows;i++){
             BigInteger c=M.A[i][k];
             if (c.signum()>0){
                   r1=RF.mult(r1, RF.pow(XF[i], c));
              }
             else{
                 //System.out.println("r2:"+r2);
                 //System.out.println(" RF:"+RF);
                 //System.out.println(" c:"+c);
                 r2=RF.mult(r2, RF.pow(XF[i], c.multiply(new BigInteger("-1"))));
              }
           }
           r1=RF.mult(r1,Utils.yexp(RF,YF[k]));
           RingElt r=RF.add(r1,r2);
           r1=RF.mult(XF[k], Utils.yexp(RF,Utils.minzero(YF[k])));
           System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+r1);
           r=RF.div(r,r1);
           System.out.println("Division successful.");
           //System.out.println("Variable " + k + " : " + X[k]);
           XF[k]=null;
           XF[k]=r;
           //System.out.println("Variable " + k +" : "+X[k]);
        }
        
        if (YF!=null){
            //System.out.print("YF not zero.");
            BigInteger[][] YFnew=new BigInteger[size][size];
            YFnew[k]=Utils.scalmult(BigInteger.ONE.negate(),YF[k]);
            hasChanged[k]=true;
            for (int j=0; j<size; j++){
                if (j!=k){
                    BigInteger[] big=new BigInteger[size];
                    big=Utils.minzero(YF[k]);
                    big=Utils.scalmult(M.A[k][j].negate(),big);
                    big=Utils.sum(big,YF[j]);
                    BigInteger c=Utils.max(BigInteger.ZERO,M.A[k][j]);
                    YFnew[j]=Utils.sum(big,Utils.scalmult(c,YF[k]));
                    boolean change=false;
                    for (int l=0;l<size;l++){
                        change=change || (!YFnew[j][l].equals(YF[j][l]));
                    }
                    hasChanged[j]=change;
                }
            }
            YF=null;
            YF=YFnew;
        }
    }
    
     public String toString(){
            String s="";
            for (int i=0; i<size;i++){
                s=s+"xf["+(i+1)+"]:="+XF[i].toString()+";\n";
            }
            for (int i=0; i<size;i++){
                s=s+"yf["+(i+1)+"]:="+Utils.yexp(RF,YF[i]).toString()+";\n";
            }
            return s;
        }
        
        public String toString(int i){
            String s="";
            s=s+"xf["+(i+1)+"]:="+XF[i].toString()+";\n";
            for (int j=0;j<size;j++){
                if (!hasChanged[j]){
                    continue;
                }
                s=s+"yf["+(j+1)+"]:="+Utils.yexp(RF,YF[j]).toString()+";\n";
            }
            return s;
        }
    
    
    public XYvariable(int narg){
        size=narg;
        Type="XY-variables";
        hasChanged=new boolean[size];
        for (int i=0;i<size;i++){
            hasChanged[i]=true;
        }
        
        String s="x1";
        int i;
        for (i=2;i<=size;i++){
            s=s+",x"+i;
        }
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
        
        XF=new RingElt[size];
        for (i=0; i<size; i++){
            XF[i]=RF.map("x"+(i+1));
            //System.out.println(X[i]);
        }
        
        YF=new BigInteger[size][size];
        for (i=0; i<size; i++){
            for (int j=0; j<size; j++){
               YF[i][j]=BigInteger.ZERO;
               if (i==j){YF[i][j]=BigInteger.ONE;}
            }
            //System.out.println(X[i]);
        }
    }
}


class XYBvariable extends Mutable{
    Ring RF;
    RingElt[] XF;
    RingElt[] YF;
    boolean[] hasChanged;
    
    void opposite_mutate(Quiver Quiv, int k, int dir){
        System.out.println("Mutate at "+k+" in direction "+dir+".");
        if (dir<0){
            for (int i=0;i<size;i++){
                YF[i]=RF.div(RF.one(),YF[i]);
                XF[i]=RF.div(RF.one(),XF[i]);
            }
        }
        mutate(Quiv, k);
        if (dir<0){
            for (int i=0;i<size;i++){
                YF[i]=RF.div(RF.one(),YF[i]);
                XF[i]=RF.div(RF.one(),XF[i]);
            }
        }
    }
    
    void mutate(Quiver Quiv, int k){
        mutate(Quiv,k,1);
    }
    
    void mutate(Quiver Quiv, int k, int dir){
        System.out.println("Mutate at "+k+" in direction "+dir+".");
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        
        if (XF!=null){
        RingElt r1=RF.one();
        RingElt r2=RF.one();
        BigInteger c;
        for (int i=0;i<M.nbrows;i++){
             if (dir>0){c=M.A[i][k];}
             else {c=M.A[k][i];}
             if (c.signum()>0){
                 r1=RF.mult(r1,RF.pow(XF[i],c));
             }
           }
           //r2=RF.div(RF.one(),YF[k]);
           if (dir>0) {r2=RF.div(RF.one(),YF[k]);}
           else {r2=YF[k];}
           r2=RF.add(RF.one(),r2);
           RingElt r=RF.mult(r2,r1);
           System.out.println("About to divide in step "+k+":\n"+"Numerator:"+r+"\nDenominator:"+r1);
           r=RF.div(r,XF[k]);
           System.out.println("Division successful.");
           //System.out.println("Variable " + k + " : " + X[k]);
           XF[k]=null;
           XF[k]=r;
           //System.out.println("Variable " + k +" : "+X[k]);
        }
        
        if (YF!=null){
            RingElt r1=RF.one();
            RingElt r2;
            RingElt oldel;
            for (int i=0;i<M.nbrows;i++){
                if (i==k){continue;}
                 BigInteger c=M.A[i][k];
                 if (c.signum()>0){
                      r2=RF.add(r1,YF[k]);
                  }
                 else{
                      r2=RF.add(r1,RF.div(r1,YF[k]));
                  }
                 oldel=YF[i];
                 YF[i]=RF.mult(YF[i], RF.pow(r2,c));
                 hasChanged[i]= (!oldel.equals(YF[i]));
                 //System.out.println("Variable Y " + i +" : "+Y[i]);
            }
            oldel=YF[k];
            YF[k]=RF.div(r1,YF[k]);
            hasChanged[k]=(!oldel.equals(YF[k]));
        }
        
        
    }
    
     public String toString(){
            String s="";
            for (int i=0; i<size;i++){
                s=s+"xks["+(i+1)+"]:="+XF[i].toString()+";\n";
            }
            for (int i=0; i<size;i++){
                s=s+"yks["+(i+1)+"]:="+YF[i].toString()+";\n";
            }
            return s;
        }
        
        public String toString(int i){
            String s="";
            s=s+"xks["+(i+1)+"]:="+XF[i].toString()+";\n";
            for (int j=0;j<size;j++){
                if (!hasChanged[j]){
                    continue;
                }
                s=s+"yks["+(j+1)+"]:="+YF[j].toString()+";\n";
            }
            return s;
        }
    
    public XYBvariable(int narg, Ring RFarg){
        size=narg;
        Type="KS-variables";
        hasChanged=new boolean[size];
        for (int i=0;i<size;i++){
            hasChanged[i]=true;
        }
        
        RF=RFarg;
        
        XF=new RingElt[size];
        
        int i;
        for (i=0; i<size; i++){
            XF[i]=RF.map("x"+(i+1));
            //System.out.println(X[i]);
        }
        
        YF=new RingElt[size];
        for (i=0; i<size; i++){
            YF[i]=RF.map("y"+(i+1));
        }
    }
    
    public XYBvariable(int narg){
        size=narg;
        Type="KS-variables";
        hasChanged=new boolean[size];
        for (int i=0;i<size;i++){
            hasChanged[i]=true;
        }
        
        String s="x1";
        int i;
        for (i=2;i<=size;i++){
            s=s+",x"+i;
        }
        for (i=1;i<=size;i++){
            s=s+",y"+i;
        }
        RF=new QuotientField(new PolynomialRing(Ring.Z, s));
        
        XF=new RingElt[size];
        for (i=0; i<size; i++){
            XF[i]=RF.map("x"+(i+1));
            //System.out.println(X[i]);
        }
        
        YF=new RingElt[size];
        for (i=0; i<size; i++){
            YF[i]=RF.map("y"+(i+1));
        }
    }
}

class Monomial implements Comparable {
    static int n;
    static Ring R;
    static RingElt V;
    static BMatrix Lam;
    RingElt r;
    int[] e;
    
    public static void initialize(BMatrix argLam){
        Lam=new BMatrix(argLam);
        n=Lam.nbrows;
        R=new QuotientField(new PolynomialRing(Ring.Z, "v"));
        V=R.map("v");
    }
    
    public void negate(){
        r=R.neg(r);
    }
    
    
    public Monomial mult(Monomial M){
        //System.out.println("Ring R: " +R);
        //System.out.println("r="+r+", M.r="+M.r);
        RingElt c=R.pow(V, Lam.bilform(e,M.e));
        RingElt r2=R.mult(r,M.r);
        r2=R.mult(r2,c);
        //System.out.println("Product="+r2);
        int[] e2=new int[e.length];
        for (int i=0; i<e.length; i++){
            e2[i]=e[i]+M.e[i];
        }
        return new Monomial(r2,e2);
    }
    
    public Monomial div(Monomial M){
    // multiplication on the right by M^{-1}
        RingElt r2=R.div(r,M.r);
        int[] e2=new int[e.length];
        for (int i=0; i<e.length;i++){
            e2[i]=e[i]-M.e[i];
        }
        RingElt c=R.pow(V,Lam.bilform(e2,M.e).negate());
        r2=R.mult(r2,c);
        return new Monomial(r2,e2);
    }
    
    public String toString(){
        String s;
        if (r.equals(R.one())){
            s="X["+e[0];
        } 
        else if (r.equals(R.neg(R.one()))){
            s="-X["+e[0];
        }
        else {
            s=r.toString()+"*X["+e[0];
        }
        for (int i=1; i<e.length; i++){
            s=s+","+e[i];
        }
        s=s+"]";
        return s;
    }
    
    public int compareTo(Object o){
        int res=0;
        Monomial M=(Monomial) o;
        R=M.R;
        if (r.equals(R.zero())){return -1;}
        if (M.r.equals(R.zero())){return 1;}
        int i=0;
        int n=e.length;
        if (e[i]<M.e[i]){res=-1;}
        if (e[i]==M.e[i]){res=0;}
        if (e[i]>M.e[i]){res=1;}
        while ((res==0)&&(i<n-1)){
            i++;
            if (e[i]<M.e[i]){res=-1;}
            if (e[i]==M.e[i]){res=0;}
            if (e[i]>M.e[i]){res=1;}
        }
        return res;
    }
    
    public Monomial(RingElt argr, int[] arge){
        r=R.map(argr);
        e=new int[arge.length];
        for (int i=0; i<e.length; i++){
            e[i]=arge[i];
        }
    }
    
    public Monomial(int[] arge){
        this(R.one(), arge);
    }
    
    public Monomial(Monomial M){
        R=M.R;
        r=R.map(M.r);
        e=new int[M.e.length];
        for (int i=0; i<e.length; i++){
            e[i]=M.e[i];
        }
    }
    
    public Monomial(RingElt argr){
        r=R.map(argr);
        e=new int[n];
        for (int i=0; i<n; i++){e[i]=0;}
        //System.out.println("Ring R=argR in Monomial(Ring argR, RingElt argr, int n): "+R);
    }
    
    public Monomial(int argi){
        r=R.map(1);
        e=new int[n];
        for (int i=0; i<n;i++){
            e[i]=0;
        }
        e[argi]=1;
    }
    
}

class Polynomial{
    Ring R;
    int n;
    Vector P;
    
    public void subtract(Monomial argM){
        Monomial M=new Monomial(argM);
        M.R=argM.R;
        //System.out.println("Ring of M: "+M.R);
        M.negate();
        P.add(M);
        normalize();
    }
    
    public Polynomial subtract(Polynomial argP){
        Polynomial S=new Polynomial(argP);
        S.negate();
        return add(S);
    }
    
    public void negate(){
        int N=P.size();
        int i;
        Monomial M;
        for (i=0; i<N;i++){
            M=(Monomial) P.elementAt(i);
            M.negate();
        }
    }
    
    public Polynomial(Ring argR, int argn){
        P=new Vector(10,1);
        R=argR;
        n=argn;
        Monomial M=new Monomial(R.zero());
        P.add(M);
    }
    
    public Polynomial(Monomial[] argM){
        n=argM[0].e.length;
        P=new Vector(argM.length, 1);
        Monomial M;
        for (int i=0; i<argM.length; i++){
            M=new Monomial(argM[i]);
            P.add(M);
        }
    }
    
    
    public Polynomial(Monomial argM){
        n=argM.e.length;
        R=argM.R;
        P=new Vector(1,1);
        //System.out.println("Ring in Polynomial(Monomial argM): " +R);
        P.add(new Monomial(argM));
    }
    
    public Polynomial(RingElt argr){
        this(new Monomial(argr));
    }
    
    public Polynomial (Polynomial argP){
        n=argP.n;
        R=argP.R;
        int N=argP.P.size();
        P=new Vector(N,1);
        for (int i=0; i<N; i++){
            P.add(new Monomial((Monomial) argP.P.elementAt(i)));
        }
    }
    
    
    public String toString(){
        String s=""+(Monomial) P.elementAt(0);
        for (int i=1;i<P.size(); i++){
            s=s+"+"+((Monomial) P.elementAt(i));
        }
        return s;
    }
    
    public void sort(){
        Collections.sort(P);
    }
    
    public boolean isMonomial(){
        return (P.size()==1);
    }
    
    public Monomial top(){
        return new Monomial((Monomial) P.lastElement());
    }
    
    public boolean isZero(){
        return R.zero().equals(top().r);
    }
    
    public void normalize(){
        Collections.sort(P);
        int i=0;
        int j=0;
        Monomial M1=null;
        Monomial M2=null;
        while (i+1<P.size()){
            M1=(Monomial) P.elementAt(i);
            M2=(Monomial) P.elementAt(i+1);
            if (M1.compareTo(M2)==0){
                M1.r=R.add(M1.r, M2.r);
                P.removeElementAt(i+1);}
            else i++;
        }
        
        i=0;
        Monomial M;
        while (i<P.size()){
            M=(Monomial) P.elementAt(i);
            if (M.r.equals(R.zero())){
               P.removeElementAt(i);
            }
            else i++;
        }
        if (P.size()==0){
            P.add(new Monomial(R.zero()));
        }
    }
    
    public void add(Monomial M){
        P.add(new Monomial(M));
        if (P.size()>100){
            System.out.println("Normalizing a polynomial with "+P.size()+" terms.");
        }
        normalize();
    }
    
    public Polynomial add(Polynomial Q){
        Polynomial S=new Polynomial(R,n);
        int N=P.size();
        int i=0;
        Monomial M=null;
        for (i=0; i<N; i++){
            M=(Monomial) P.elementAt(i);
            S.P.add(new Monomial(M));
        }
        N=Q.P.size();
        for (i=0; i<N; i++){
            M=(Monomial) Q.P.elementAt(i);
            S.P.add(new Monomial(M));
        }
        S.normalize();
        return S;
    }
    
    public Polynomial div(Polynomial P2){
        if (Math.min(P.size(),P2.P.size())>10){
            System.out.println("Division of a polynomial with "+P.size()+" terms by a polynomial with "+P2.P.size()+" terms.");
        }        
        Polynomial Rest=new Polynomial(this);
        Polynomial Quot=new Polynomial(R.zero());
        Monomial P2top=P2.top();
        Monomial Resttop=Rest.top();
        Monomial M;
        Polynomial PM;
        Polynomial Prod;
        
        System.out.println("Dividend: "+this+ "\nDivisor: "+P2+"\nDividend top: "+Resttop+ " Divisor top: "+P2top);
        
        if (P2.isMonomial()){
            System.out.println("Quotient by Monomial.");
            for (int i=0; i<P.size();i++){
                M=(Monomial) P.elementAt(i);
                Quot.add(M.div(P2top));
            }
            return Quot;
        }
        
        int cter=0;
        while((!Rest.isZero())&&(cter<1000000)){
            //System.out.print("Rest top: "+Resttop+ " Divisor top: "+P2top);
            M=Resttop.div(P2top);
            //System.out.println("Top monomial quotient: "+M);
            Quot.add(M);
            PM=new Polynomial(M);
            ////System.out.println("Lambda:\n" + Monomial.Lam);
            Prod=PM.mult(P2);
            Rest=Rest.subtract(Prod);
            System.out.println("Quotient: "+Quot+"\nProduct: "+Prod+"\nRest: "+Rest);
            Resttop=Rest.top();
            cter++;
        }
        return Quot;
    }
    
    public Polynomial mult(Polynomial Q){
        Polynomial S=new Polynomial(R,n);
        int NP=P.size();
        int NQ=Q.P.size();
        if (NP*NQ>20){
            System.out.println("Product with "+NP*NQ+" terms.");
        }
        Monomial M1, M2;
        int i,j;
        for (i=0; i<NP;i++){
            M1=(Monomial) P.elementAt(i);
            for (j=0; j<NQ; j++){
                M2=(Monomial) Q.P.elementAt(j);
                S.P.add(M1.mult(M2));
            }
        }
        S.normalize();
        return S;
    }
    
    public Polynomial mult(RingElt argr){
        return this.mult(new Polynomial(argr));
    }
    
    public Polynomial pow(BigInteger N){
        if (N.compareTo(BigInteger.ZERO)==0){
            return new Polynomial(R.one());
        }
        Polynomial S=new Polynomial(R,n);
        BigInteger two=BigInteger.valueOf(2);
        BigInteger Ndiv2=N.divide(two);
        int Nmod2=N.remainder(two).intValue();
        if (Nmod2==0){
            Polynomial T=pow(Ndiv2);
            return T.mult(T);}
        else {
            BigInteger Nm1=N.add(BigInteger.ONE.negate());
            return this.mult(this.pow(Nm1));
        }
    }
    
}


class Cluster {
    boolean showNumbers;
    Vector Mutables;
    int size;
   
    public void setShowNumbers(boolean sn){
        showNumbers=sn;
    }
    
    public boolean getShowNumbers(){
        return showNumbers;
    }
    
    
    public void removeAllMutables(){
        Mutables.removeAllElements();
    }
    public void removeMutable(String Name){
        int remnber=-1;
        for (int i=0; i<Mutables.size();i++){
            Mutable mut=(Mutable) Mutables.elementAt(i);
            String mutName=mut.getType()+" ("+mut.getIdNber()+")";
            if (mutName.equals(Name)){
                remnber=i;
            }
        }
        if (remnber>=0){
            Mutable mut=(Mutable) Mutables.elementAt(remnber);
            Mutables.remove(remnber);
            mut=null;
        }
    }
    
    public Mutable getMutable(String Name){
        Mutable result=null;
        for (int i=0; i<Mutables.size(); i++){
            Mutable mut=(Mutable) Mutables.elementAt(i);
            String mutName=mut.getType()+" ("+mut.getIdNber()+")";
            if (mutName.equals(Name)){
                result=mut;
            }
        }
        return result;
    }
    
    public Mutable getLastMutableOfType(String requiredType){
        Mutable result=null;
        for (int i=0; i<Mutables.size(); i++){
            Mutable mut=(Mutable) Mutables.elementAt(i);
            String mutType=mut.getType();
            if (mutType.equals(requiredType)){
                result=mut;
            }
        }
        return result;
    }
    
    public void addMutable(Mutable toAdd){
        int mutnber;
        
        if (Mutables.size()==0){
            mutnber=1;
        }
        else {
            int lastindex=Mutables.size()-1;
            mutnber=1+((Mutable) Mutables.elementAt(lastindex)).getIdNber();
        }
        toAdd.setIdNber(mutnber);
        Mutables.add(toAdd);
    }
    
    public boolean hasData(){
        return ((Mutables.size()>0));
    }
    
    
    public String showMatrix(BigInteger[][] M){
        String s="";
        if (M!=null){
            for (int i=0; i<size; i++){
                s=s+M[i][0];
                for (int j=1; j<size; j++){
                    s=s+","+M[i][j];
                }
            s=s+"\n";
            }
        }
        return s;
    }
    
       /*
    public void write(BufferedWriter out){
    String str;
    try{
    out.write(""+size); out.newLine();
        if (variablesActive()){
            PolynomialRing S=(PolynomialRing) ((QuotientField) R).getBaseRing();
            str="";
            for (int i=0; i<size-1; i++){
                str=","+S.getVariable() + str;
                //System.out.println(str);
                S=(PolynomialRing) S.getCoefficientRing();
            }
            str=S.getVariable() + str;
            //System.out.println(str);
            out.write(str); out.newLine();
            for (int i=0; i<size; i++){
                out.write(X[i].toString());
                out.newLine();
            }
        }
        if (dvectorsActive()){
            for (int i=0; i<size; i++){
                str=D.A[i][0].toString();
                for (int j=1; j<size; j++){
                    str=str+" "+D.A[i][j];
                }
                out.write(str); out.newLine();
            }
        }
    }
    catch (IOException e){
        System.out.println(e.getMessage());
    }
    //System.out.println(((QuotientField) R).getBaseRing());
    }
*/
    
    public void write(BufferedWriter out){
	String str;
	try{
	out.write(""+size); out.newLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
	//System.out.println(((QuotientField) R).getBaseRing());
    }

    public void read(BufferedReader in, String instructions){
	String str, patternstr;
        String[] fields;
        int i,j;

	try{
	    str=in.readLine();
	    //System.out.println("Cluster's first string: "+str);
	    size=Integer.parseInt(str);
            /*
            boolean v=(instructions.indexOf("Variables")>0);
            boolean d=(instructions.indexOf("Dimensions")>0);
            if (v || (!(v || d))){
                str=in.readLine();
                //System.out.println("Size: "+size + " Variables: "+str);
                QuotientField S=new QuotientField(new PolynomialRing(Ring.Z,str));
                R=null;
                R=S;
                //System.out.println(R);
                RingElt[] Y=new RingElt[size];
                for (i=0; i<size; i++){
                    str=in.readLine();
                    //System.out.println(str);
                    Y[i]=S.map(str);
                }
                X=null;
                X=Y;
            }
            if (d){
                i=0;
                patternstr=" ";
                while (i<size) {
                    str=in.readLine();
                    fields = str.split(patternstr);
                    for(j=0; j<size; j++){
                        D.A[i][j]=new BigInteger(fields[j]);
                    }
                    i++;
                }
            }
             */
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
    
    

    /*
    public void permuteVertices(int[] perm){
       int[] invperm=Seed.invPerm(perm);
        if (X!=null){
            RingElt[] Y=new RingElt[size];
            for (int i=0; i<size; i++){
                StringBuffer temp=new StringBuffer(X[perm[i]].toString());
                for (int j=0; j<size; j++){
                    replaceAll(temp,"x"+(j+1),"y"+(invperm[j]+1));
                }
                replaceAll(temp,"y","x");
                //System.out.println("Final: "+temp);
                Y[i]=R.map(temp.toString());
            }
            X=null;
            X=Y;    
        }
    }
    
    
    public void addvar(){
        if (X==null){
            return;
        }
        
        int i, sizenew;
        RingElt[] Xnew;
        String s;
        
        //System.out.println(toString());
        sizenew=size+1;
        s="x0";
        for (i=1;i<sizenew;i++){
            s=s+",x"+i;
        }
         
        //System.out.println(s);
        Ring Rnew= new QuotientField(new PolynomialRing(Ring.Z,s));
        Xnew = new RingElt[sizenew];
        
        for (i=0;i<size;i++){
            Xnew[i]=Rnew.map(X[i]);
            //System.out.println("X"+i+":"+X[i]);
            //System.out.println("Xnew"+i+":"+Xnew[i]);
        }
        Xnew[sizenew-1]=Rnew.map("x"+(sizenew-1));
        
        R=null;
        R=Rnew;
        X=null;
        X=Xnew;
        size=sizenew;
        //System.out.println(toString());
       
    }
 */
    
    
    
    public JMenu getDeactivateMenu(QuiverDrawing qd){
        JMenu dm=new JMenu("Deactivate");
        //System.out.println("Producing Deactivate menu");
        //System.out.println("Mutables.size()="+Mutables.size());
        JMenuItem mi=new JMenuItem("All");
        mi.addActionListener(qd);
        dm.add(mi);
        for (int i=0; i<Mutables.size(); i++){
            Mutable mut=(Mutable) Mutables.elementAt(i);
            mi=new JMenuItem(mut.getType()+" ("+mut.getIdNber()+")");
            mi.addActionListener(qd);
            dm.add(mi);
        }
        return dm;
    }
    
    
        
    
    public static BigInteger[] polyDegree(Ring R, RingElt r){
           
        PolynomialRing R1=(PolynomialRing) ((QuotientField) R).getBaseRing();
        String s=R1.toString();
        String[] fields = s.split("y");
        int size=fields.length-1;
        BigInteger[] polydeg=new BigInteger[size];

        for (int j=0;j<size;j++){
            //System.out.println(R1);
            int d=R1.degree(r);
            //System.out.println("R1, r, j,d:"+R1+","+r+", " +j+"," +d);
            polydeg[size-1-j]=new BigInteger(""+d);
            r=R1.getCoefficientAt(d,r);
            if (j+1<size){R1=(PolynomialRing) R1.getCoefficientRing();}
        }   
        return polydeg;
        
    }
    
    
    public Cluster(int sz){
        int i;
        String s;
        
        size=sz;
        Mutables=new Vector(10,1);
    }
    
    
    public void reset(){
        removeAllMutables();
    }
    
   
    
    public static String vecstring(BigInteger[] v){
        String s=v[0].toString();
        for (int i=1;i<v.length;i++){
            s=s+","+v[i].toString();
        }
        return s;
    }
    
    
    public void mutate(Quiver Quiv, int k, int dir){
        
        //System.out.println("Entering cluster mutation.");
        BMatrix M=new BMatrix(size,size);
        M.copyfrom(Quiv.M);
        
        if (Mutables.size()>0){
            for (int i=0; i<Mutables.size();i++){
                ((Mutable) Mutables.elementAt(i)).mutate(Quiv, k, dir);
            }
        }
   }
        
   public void permuteVertices(int[] perm){
       if (Mutables.size()>0){
           for (int i=0; i<Mutables.size();i++){
               ((Mutable) Mutables.elementAt(i)).permuteVertices(perm);
           }
       }
   }
    
    public String toString(){
        int i;
        String s="";
        Mutable mut;
        if (Mutables.size()>0){
            for (int j=0; j<Mutables.size();j++){
                mut=(Mutable) Mutables.elementAt(j);
                if (showNumbers){
                    s=s+mut.getIdNber()+": ";
                }
                s=s+((Mutable) Mutables.elementAt(j)).toString();
            }
        }
        return s;
    }
    
    public String toString(int i){
        String s="";
        Mutable mut;
        if (Mutables.size()>0){
            for (int j=0; j<Mutables.size();j++){
                mut=(Mutable) Mutables.elementAt(j);
                if (showNumbers){
                    s=s+mut.getIdNber()+": ";
                }
                s=s+((Mutable) Mutables.elementAt(j)).toString(i);
            }
        }
        return s;
    }
}




class Quiver  {
    public static final int DATAFILE=0;
    public static final int GLSA3=1;
    public static final int GLSA4=2;
    public static final int GLSAN=3;
    public static final int quiver234=4;
    //public static final double arrowheadwidth=0.16;
    //public static final double arrowheadlength=0.2;
    public static final double arrowheadwidth=0.2;
    public static final double arrowheadlength=0.25;

    QuiverDrawing qd;
    int quiverType;
    int parameter;
    String quiverFilename;
    BMatrix M;
    BMatrix oldM;
    BMatrix Lambda;
    CMatrix CM;
    SMatrix StyleMatrix;
    SMatrix oldStyleMatrix;
    BigInteger[] Oxvector;
    double[][] RCharges;
    int nbpoints, inew;
    MoveablePoint[] P;
    MoveablePoint[] oldP;
    History Hist;
    History oldHist;
    SequencesDialog SeqDia;
    int vertexradius;
    boolean showLabels;
    boolean showFrozenVertices;
    boolean trafficLights;
    boolean shortNumbers;
    float growthFactor;
    float arrowlabelsize;
    
    int[] simples;
    BMatrix dimVect;
    boolean showSpikes;
    Vector submodules;

    int blueprintnbpoints;
    BMatrix blueprintM;
    Vector word;
    String taumutseq, tauperm;
    int[] taufirstoccur;
    int tauorder;
    
    public BigInteger weight(){
        return M.weight();
    }
    
    public void setColor(Color col){
        for (int i=0; i<nbpoints; i++){
            if (!P[i].frozen) {P[i].setColor(col);}
        }
    }

    public void read(BufferedReader in){
	String str;
	try{
	    if (qd.lastReadLine!=null){
                str=qd.lastReadLine;
            }
            else {
                str=in.readLine();
            }
            //System.out.println("Reading quiver.");
            //System.out.println("read quiver 1: "+str);
	    str=in.readLine();
	    //System.out.println("read quiver 2: "+str);
	    nbpoints=Integer.parseInt(str);
	    str=in.readLine();
            //System.out.println("read quiver 3: "+str);
	    str=in.readLine();
            //System.out.println("read quiver 4: "+str);
	    vertexradius=Integer.parseInt(str);
	    str=in.readLine();
            //System.out.println("read quiver 5: "+str);
	    str=in.readLine();
            //System.out.println("read quiver 6: "+str);
	    if (str.equals("1")){showLabels=true;}
	    else {showLabels=false;}
            showFrozenVertices=true;
            trafficLights=false;
            Oxvector=null;
            if (SeqDia!=null){SeqDia.dispose();}
            SeqDia=null;
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
	M.read(in);
        CM=null;
        Lambda=null;
        StyleMatrix=null;
        str="";
	try{
	    str=in.readLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
        if ("//Growth factor".equals(str)){
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage()); 
            }
            growthFactor=Float.parseFloat(str);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//Arrow label size".equals(str)){
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage()); 
            }
            arrowlabelsize=Float.parseFloat(str);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//Style Matrix".equals(str)){
            StyleMatrix=new SMatrix(in);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//Colored Matrix".equals(str)){
            CM=new CMatrix(in);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        taumutseq=null;
        tauperm=null;
        tauorder=-1;
        if ("//tau-data".equals(str)){
            try{
                taumutseq=in.readLine();
                tauperm=in.readLine();
                str=in.readLine();
                tauorder=Integer.parseInt(str);
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//Lambda".equals(str)){
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            Lambda=new BMatrix(in);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//Traffic lights".equals(str)){
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            trafficLights=str.equals("1");
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        dimVect=null;
        simples=null;
        submodules=null;
        showSpikes=false;
        if ("//dimVect".equals(str)){
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            dimVect=new BMatrix(in);
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ("//submodules".equals(str)){
            submodules=new Vector(10);
            int[] p;
            String[] fields;
            for (int i=0; i<nbpoints-1;i++){
                try{
                    str=in.readLine();
                }
                catch (IOException e){
                    System.out.println(e.getMessage());
                }
                if (str.length()>0){
                   fields=str.split(" ");
                   p=new int[fields.length];
                   for (int j=0; j<fields.length; j++){
                       p[j]=Integer.parseInt(fields[j]);
                   }
                   submodules.add(p);
               }
               else {
                   p=new int[0];
                   submodules.add(p);
               }
            }
            try{
                    str=in.readLine();
                }
                catch (IOException e){
                    System.out.println(e.getMessage());
                }
        }
        MoveablePoint[] Pnew=new MoveablePoint[nbpoints];
	for (int i=0; i<nbpoints;i++){
            Pnew[i]=new MoveablePoint(0,0,vertexradius);
	    Pnew[i].read(in);
	}
        P=null;
        P=Pnew;
        Hist.read(in);
        //System.out.println(Hist);
        //System.out.println("History buttons update in quiver.read()");
        //Hist.updatebuttons();
        //System.out.println("qd.lastreadLine after Hist.read(in): "+ qd.lastReadLine);
        if (qd.lastReadLine!=null){str=qd.lastReadLine;}
        else {
            try{
                str=in.readLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
                str=null;
            }
        }
        //System.out.println("read quiver 7: "+str);
        if ("//Sequences dialog".equals(str)){
            qd.lastReadLine=null;
            SeqDia=new SequencesDialog(qd);
            SeqDia.read(in);
            if ((taumutseq!=null) && (tauperm!=null)){
                SeqDia.setSequence(taumutseq);
                SeqDia.setPerm(tauperm);
            }
        }
        else {
            qd.setLastReadLine(str);
            if (SeqDia!=null){
                SeqDia.dispose();
                SeqDia=null;
            }
        }
        if (dimVect!=null){
            if (simples==null){determineSimples(true);} // origin already present
            if (submodules==null){determineSubmodules();}
            showSpikes=true;
            qd.updatestatus(qd.MODIFYING_CENTRAL_CHARGE);
        }
        else {
            qd.updatestatus(qd.MUTATING);
        }
        if (trafficLights){
            qd.trafficLightsItem.setText("Switch traffic lights off");
        }
        else{
            qd.trafficLightsItem.setText("Switch traffic lights on");
        }
            
    }
    
	    

    public void write(BufferedWriter out){
	try{ 
	    out.write("//Number of points"); out.newLine();
	    out.write(""+nbpoints); out.newLine();
	    out.write("//Vertex radius"); out.newLine();
	    out.write(""+vertexradius); out.newLine();
	    out.write("//Labels shown"); out.newLine();
	    if (showLabels){
		out.write("1");
	    }
	    else {
		out.write("0");
	    }
	    out.newLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
	M.write(out);
        try{
            out.write("//Growth factor"); out.newLine();
            out.write(""+growthFactor); out.newLine();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        try{
            out.write("//Arrow label size"); out.newLine();
            out.write(""+arrowlabelsize); out.newLine();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        if (StyleMatrix!=null){
            StyleMatrix.write(out);
        }
        if (CM!=null){
            CM.write(out);
        }
        if (taumutseq!=null){
            try{
            out.write("//tau-data"); out.newLine();
            out.write(taumutseq); out.newLine();
            out.write(tauperm); out.newLine();
            out.write(""+tauorder); out.newLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if (Lambda!=null){
            try{
            out.write("//Lambda"); out.newLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            Lambda.write(out);
        }
        if (true){
            try{
                out.write("//Traffic lights"); out.newLine();
                if (trafficLights){
                    out.write("1");
                }
                else {
                    out.write("0");
                }
                out.newLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
        if ((dimVect!=null)&&(submodules!=null)){
            try{
                out.write("//dimVect");out.newLine();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
            dimVect.write(out);
            try{
                out.write("//submodules");out.newLine();
                for (int i=0; i<nbpoints-1; i++){
                    out.write(Utils.toString((int[]) submodules.elementAt(i)));
                    out.newLine();
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
	try{
            out.write("//Points"); out.newLine();
            }
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
	int i;
	for (i=0; i<nbpoints; i++){
	    P[i].write(out);
	}
        Hist.write(out);
        if (SeqDia!=null){
            SeqDia.write(out);
        }
    }
    
    
    public String tojvx(int option){
        int i,j;
        double x,y,z,t,t1, middley, minx, miny, minz, maxx, maxy,maxz;
        String s,str;
        
        Rectangle r=strictEnclosingRectangle();
        double R=r.width/(2*Math.PI);
        middley=r.y+r.height/2;
        minx=10000;
        miny=minx;
        minz=minx;
        maxx=-10000;
        maxy=maxx;
        maxz=maxx;
        
        s="<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>"+"\n";
        s=s+"<!DOCTYPE jvx-model SYSTEM \"http://www.javaview.de/rsrc/jvx.dtd\">"+"\n";
        s=s+"<jvx-model>"+"\n";
        s=s+"<version type=\"dump\">0.10</version>";
        s=s+"<title>Quiver mutation</title>"+"\n";
        s=s+"<geometries>\n";
        s=s+"<geometry name=\"Quiver\">\n";
        s=s+"<pointSet point=\"show\" dim=\"3\">\n";
        s=s+"<points num=\""+nbpoints+"\">\n";
        x=0; y=0; z=0;
        for (i=0; i<nbpoints;i++){
            t=P[i].getX()/R;
            switch (option){
              case 0:
                x=0.1*P[i].getX();
                y=0.1*P[i].getY();
                z=0.1*P[i].height;
              break;
              case 1:
                x=0.1*(R+P[i].height)*Math.cos(t);
                y=0.1*(R+P[i].height)*Math.sin(t);
                z=0.1*P[i].getY();
              break;
                case 2:
                t1=t/2;
                z=P[i].getY()-middley;
                x=0.1*Math.cos(t)*(R+Math.sin(t1)*z);
                y=0.1*Math.sin(t)*(R+Math.sin(t1)*z);
                z=0.1*Math.cos(t1)*z;
                break;
            }
            s=s+"<p>"+ x+ " "+y+" "+z+"</p>\n";
            if (x<minx){minx=x;}
            if (x>maxx){maxx=x;}
            if (y<miny){miny=y;}
            if (y>maxy){maxy=y;}
            if (z<minz){minz=z;}
            if (z>maxz){maxz=z;}
        }
        Color c=P[nbpoints-1].mycolor;
        s=s+"<thickness>8.0</thickness>\n<color type=\"rgb\">"+c.getRed()+" "+ c.getGreen()+" "+  c.getBlue()+ 
                "</color>\n<colorTag type=\"rgb\">"+c.getRed()+" "+ c.getGreen()+" "+  c.getBlue()+ "</colorTag>\n";
        s=s+"</points>\n</pointSet>\n";
        s=s+"<lineSet line=\"show\">\n<lines num=\"";
        int linenber=0;
        for (i=0; i<nbpoints; i++){
            for (j=0; j<nbpoints; j++){
                if (M.A[i][j].signum()>0){
                    linenber++;
                }
            }
        }
        s=s+linenber+"\">\n";
        for (i=0; i<nbpoints; i++){
            for (j=0; j<nbpoints; j++){
                if (M.A[i][j].signum()>0){
                    s=s+"<l>"+i+" "+j+"</l>";
                }
            }
        }
        s=s+"<thickness>4.0</thickness>\n";
        s=s+"<color type=\"rgb\">127 137 250</color>\n<colorTag type=\"rgb\">255 0 255</colorTag>\n</lines>\n";
        s=s+"</lineSet>\n";
        s=s+"<bndbox visible=\"hide\">\n";
        s=s+"<p>"+minx+" "+miny+ " "+minz+"</p>\n";
        s=s+"<p>"+maxx+" " + maxy+" "+maxz+"</p>\n";
        s=s+"</bndbox>\n";
        s=s+"<center visible=\"hide\">\n<p>0 0 0</p>\n</center>\n</geometry>\n</geometries>\n</jvx-model>";
        
        return s;
    }
    
    public String toxy(){
        int i,j,x,y, tx,ty ;
        String s, str;
        BigInteger un, moinsun;
        
        tx=-strictEnclosingRectangle().x;
        ty=-strictEnclosingRectangle().y;
    
        s="\\begin{xy} 0;<1pt,0pt>:<0pt,-1pt>:: \n";
        for (i=0; i<nbpoints; i++){
            x=(int)(P[i].getX());
            y= (int) (P[i].getY());
            //s=s+"("+x+","+y+") *\\cir<"+P[i].r+"pt>{} *+{"+i+"} ="+"\""+i+"\","+"\n";
            if (showLabels){str=""+(i+1);} else {str="\\circ";}
                s=s+"("+(x+tx)+","+(y+ty)+") *+{"+str+"} ="+"\""+i+"\","+"\n";
            }
        
        un = new BigInteger("1");
        moinsun=new BigInteger("-1");
        BigInteger v1=null;
        BigInteger v2=null;
        for(i=0;i<M.nbrows;i++){
        for(j=i+1;j<M.nbcols;j++){
        if (!((P[i].frozen)&(P[j].frozen))){
            v1=M.A[i][j].abs();
            v2=M.A[j][i].abs();
            str="";
            if ((v1.compareTo(un)>0)||(v2.compareTo(un)>0)){
                 if (v1.compareTo(v2)==0){
                      str=""+v1;
                  }
                  else{
		      if (M.A[i][j].signum()>0){
                      str=""+v1+","+v2;
			  }
			  else {
			      str=""+v2+","+v1;
			  }
                  }
                 str="|*+{\\scriptstyle "+str+"}";
            }
             if(M.A[i][j].signum() >0){
                s=s+"\""+i+"\", {\\ar"+str+"\""+j+"\"},\n";
             }
             if(M.A[i][j].signum() <0){
                s=s+"\""+j+"\", {\\ar"+str+"\""+i+"\"},\n";
             }
        }}}
        
        s=s+"\\end{xy}";
        return s;
    }
    
    public String toString(){
	int i;
	String s;

	s="Quiver type:" + quiverType + "\n";
	s=s+"Parameter:" + parameter + "\n";
	s=s+"Number of points:" + nbpoints + "\n";
	s=s+"Vertex radius:" + vertexradius + "\n";
	s=s+"Show labels:" + showLabels+ "\n";
	s=s+M.toString();
	for (i=0;i<nbpoints;i++){
	    s=s+P[i].toString()+"\n";
	}
	return s;
    }
	
	
    public void restoreQuiver(){
        M=oldM;
        P=oldP;
        nbpoints=P.length;
        Hist=oldHist;
        StyleMatrix=oldStyleMatrix;
    }
    
    public boolean remembersOldQuiver(){
        return (oldM!=null);
    }
    
    public void deleteFrozenNodes(){
        int i=0;
        while (i<nbpoints){
            if (P[i].frozen){
                deletenode(i);
            }
            else {
                i++;
            }
        }
    }

    
    public void deletenode(int idel){
        
        int[] tauseqints=null;
        int[] taupermints=null;
        
        if (SeqDia!=null){
            String tauseq=SeqDia.sequenceField[0].getText();
            String tauperm=SeqDia.permField[0].getText();
            System.out.println(tauseq);
            System.out.println(tauperm);
            String[] tauseqcomps=tauseq.split(" ");
            tauseqints=new int[tauseqcomps.length];
            for (int i=0;i<tauseqcomps.length;i++){
                tauseqints[i]=Integer.parseInt(tauseqcomps[i]);
                System.out.println("i="+i+" "+tauseqints[i]);
            }
            String[] taupermcomps=tauperm.split(" ");
            taupermints=new int[taupermcomps.length];
            for (int i=0;i<taupermcomps.length;i++){
                taupermints[i]=Integer.parseInt(taupermcomps[i]);
                System.out.println("i="+i+" "+taupermints[i]);
            }
        }
        
        
        
        BMatrix newM;
        SMatrix newS;
        
        oldM=M;
        oldP=P;
        oldStyleMatrix=StyleMatrix;
        oldHist=Hist;
        
        int newnbpoints;
        int i;
        MoveablePoint[] newP;

        //System.out.println("Delete node : "+idel);
        newnbpoints=nbpoints-1;

        newP=new MoveablePoint[newnbpoints];
        
        
        int[] oldtonew=new int[nbpoints];
        oldtonew[idel]=-10;
        
        inew=0;
        for (i=0;i<nbpoints;i++){
           if (i!=idel){
               oldtonew[i]=inew;
               newP[inew]=P[i]; inew++;}
        }
        
        for (i=0;i<nbpoints;i++){
            System.out.println("Old:"+(i+1)+" New:"+(oldtonew[i]+1));
        }
        
        if (tauseqints!=null){
            for (i=0;i<tauseqints.length;i++){
                System.out.println("i="+i+" tauseqints[i]-1="+(tauseqints[i]-1));
                tauseqints[i]=oldtonew[tauseqints[i]-1]+1;
            }
            for (i=0;i<taupermints.length;i++){
                System.out.println("i="+i+" taupermints[i]-1="+(taupermints[i]-1));
                if (taupermints[i]-1>=0){
                    taupermints[i]=oldtonew[taupermints[i]-1]+1;
                }
            }
            String tauseq=""+tauseqints[0];
            for (i=1;i<tauseqints.length;i++){
                tauseq=tauseq+" "+tauseqints[i];
            }
            String tauperm=""+taupermints[0];
            for (i=1;i<taupermints.length;i++){
                tauperm=tauperm+" "+taupermints[i];
            }
            SeqDia.sequenceField[0].setText(tauseq);
            SeqDia.permField[0].setText(tauperm);
        }
        
        
        

        newM=new BMatrix(M.nbrows-1,M.nbcols-1);
        newM.copyfrom(M, idel);
        newS=null;
        if (StyleMatrix!=null){
            newS=new SMatrix(M.nbrows-1,M.nbcols-1);
            newS.copyfrom(StyleMatrix,idel);
        }
            
        if (dimVect!=null){
            System.out.println("idel="+idel+" dimVect.nbrows="+dimVect.nbrows);
            if (idel<=dimVect.nbrows-1){
                System.out.println("1. dimvect:\n"+dimVect);
                    BMatrix newdimVect=new BMatrix(dimVect.nbrows,dimVect.nbcols);
                    newdimVect=dimVect.removeRow(idel);
                    dimVect=newdimVect;
                    System.out.println("2. dimvect:\n"+dimVect);
                    System.out.println(dimVect.toString());
                    if (simples!=null){
                        determineSimples(true); // origin already present
                        determineSubmodules();
                    }
            }
        }

        nbpoints=newnbpoints;

        Hist=new History(qd);
        M=null; 
        P=null;
        M=newM;
        StyleMatrix=newS;
        Lambda=null;
        P=newP;
    }
    
    
    public void mutate(int k, int dir){
           M.mutate(k);
           if (CM!=null){CM.mutate(k,dir);}
           if (Lambda!=null){Lambda.mutateViaCongruence(M,k);}
           //System.out.println("Mutate at: "+k);
           if (Oxvector!=null){
               BigInteger sum=BigInteger.ZERO;
               for (int l=0; l<nbpoints; l++){
                   if (M.A[k][l].compareTo(BigInteger.ZERO)>0){
                       sum=sum.add(Oxvector[l].multiply(M.A[k][l]));
                   }
               }
               Oxvector[k]=sum.subtract(Oxvector[k]);
               //System.out.println("Oxvector:" + Utils.toString(Oxvector));
               
               
               int i=0;
               int[] cycle=null;
               while ((cycle==null) && (i<nbpoints)){
                    cycle=cycleThrough(i);
                    i=i+1;
               }
               if (cycle==null){
                   System.out.println("The quiver contains no cycle.");
               }
               else {
                   System.out.println("Chosen cycle:"+Utils.toString(cycle));
               }
               
               if ((cycle!=null) && (Utils.omnipresent(Oxvector))){
                    boolean ChargeSumNonZero=setRCharges(cycle);
                    if (ChargeSumNonZero){
                        computeAngles();
                        scalecenter(qd.getBounds());
                    }
                }
           }
    }
    
    public boolean maxMultExceeds(int mm){
        return M.maxMultExceeds(new Integer(mm)).booleanValue();
    }
 
    public boolean hasDoubleArrow(){
        BigInteger two=new BigInteger("2");
        boolean found=false;
        int i=0;
        int j;
        while ((i<nbpoints)&(!found)){
            j=i+1;
            while ((j<nbpoints)&(!found)){
                if (M.A[i][j].abs().equals(two)){
                    found=true;
                }
                j++;
            }
            i++;
        }
        return found;
    }
    
    public boolean pathfromto(int from, int to, int maxlength, Vector p){
       boolean found=false;
       if (M.A[from][to].compareTo(BigInteger.ZERO)>0){
           p.add(new Integer(to));
           found=true;
       }
       else {
          int j=0;
          do {
               if (M.A[from][j].compareTo(BigInteger.ZERO)>0){
                   p.add(new Integer(j));
                   found=pathfromto(j, to, maxlength-1, p);
               }
               j=j+1;
          }
          while ((j<nbpoints) && (!found) && (p.size()<maxlength));
       }
       return found;
    }
    
    public int[] cycleThrough(int i){
        int[] cycle;
        Vector p=new Vector(10,1);
        p.add(new Integer(i));
        int maxlength=weight().intValue();
        if (pathfromto(i,i, maxlength, p)){
            cycle=new int[p.size()];
            for (int j=0; j<p.size(); j++){
                cycle[j]=((Integer) p.elementAt(j)).intValue();
                //System.out.println(cycle[j]);
            }
        }
        else {
            cycle=null;
        }
        return cycle;
    }
    
    public void setDefaultColors(){
        for (int i=0; i<nbpoints; i++){
            if (P[i].frozen){
                P[i].setColor(Color.BLUE);
            }
            if (!P[i].frozen){
                P[i].setColor(Color.RED);
            }
        }
    }

    public void updateTrafficLights(){
        if (!trafficLights){
           return;}
        
        for (int i=0; i<nbpoints; i++){
            if (!P[i].frozen){
                P[i].setColor(Color.GREEN);
            }
        }

        for (int i=0; i<nbpoints; i++){
            if (P[i].frozen){
                for (int j=0; j<nbpoints; j++){
                    if (M.A[i][j].compareTo(BigInteger.ZERO)>0){
                        P[j].setColor(Color.RED);
                    }
                }
            }
        }
    }
    
    public boolean isGentleVertex(int i){
        BigInteger sp=BigInteger.ZERO;
        BigInteger sm=BigInteger.ZERO;
        BigInteger c;
        for (int j=0; j<nbpoints; j++){
            c=M.A[i][j];
            if (c.compareTo(BigInteger.ZERO)>0){
                sp=sp.add(c);
            }
            if (c.compareTo(BigInteger.ZERO)<0){
                sm=sm.add(c.negate());
            }
        }
        BigInteger two=new BigInteger("2");
        if ((sp.compareTo(two)<=0) && (sm.compareTo(two)<=0)){
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean isAcyclic(){
        return M.isAcyclic();
    }
    
    public boolean isSource(int i){
        return M.isSource(i);
    }
    
    public boolean isSink(int i){
        return M.isSink(i);
    }
    
    public int[] sources(){
        return M.sources();
    }
    
    public int[] sinks(){
        return M.sinks();
    }
    
    public boolean isSinkSource(int i){
        int globalsign=0;
        boolean throughpass=false;
        int j=0;
        while ((!throughpass)&&(j<nbpoints)){
            int sig=M.A[i][j].signum();
            if (sig!=0){
                if (globalsign==0){
                    globalsign=sig;
                }
                else {
                    throughpass=(globalsign!=sig);
                }
            }
            j++;
        }
        return !throughpass;
    }
    
    public int[] sinksSources(){
        int[] ss=new int[nbpoints];
        int cter=0;
        for (int i=0; i<nbpoints; i++){
            if (isSinkSource(i)){
                ss[cter]=i; 
                cter++;
            }
        }
        int[] res=new int[cter];
        for (int i=0; i<cter; i++){
            res[i]=ss[i];
        }
        return res;
    }
    
    public int firstSource(int i){
        boolean found=false;
        int j=0;
        
        while ((j<this.nbpoints) && (!found)){
            if (M.A[j][i].compareTo(BigInteger.ZERO)>0){found=true;}
            if (!found){j++;}
        }
        if (found){
            return j;
        }
        else {
            return -1;
        }
    }
    
    public int firstTarget(int i){
        boolean found=false;
        int j=0;
        
        while ((j<this.nbpoints) && (!found)){
            if (M.A[j][i].compareTo(BigInteger.ZERO)<0){found=true;}
            if (!found){j++;}
        }
        if (found){
            return j;
        }
        else {
            return -1;
        }
    }
    
    public int[] immediatePredec(int i){
        return M.immediatePredec(i);
    }
    
    public int[] predec(int i){
        if (submodules!=null){
            if (submodules.size()>i){
                return (int[]) submodules.elementAt(i);
            }
        }
        int[] impredec=immediatePredec(i);
        //System.out.println("Immediate predecessors of i="+i);
        //System.out.println(Utils.toString(impredec));
        Vector v=new Vector(10);
        int[] p;
        for (int j=0; j<impredec.length; j++){
            if (Utils.isLessOrEqual(dimVect.A[impredec[j]], dimVect.A[i])){
            //if (HomLessEqual(impredec[j], i)){
                    v.add(new Integer(impredec[j]));}
            p=predec(impredec[j]);
            //System.out.println("Immediate predecessors of "+j);
            //System.out.println(Utils.toString(p));
            Integer toadd;
            for (int k=0;k<p.length;k++){
                if (Utils.isLessOrEqual(dimVect.A[p[k]], dimVect.A[i])){
                //if (HomLessEqual(p[k], i)){
                    toadd=new Integer(p[k]);
                    if (v.indexOf(toadd)==-1){
                        v.add(new Integer(p[k]));
                    }
                }
            }
        }
        
        int[] pred=new int[v.size()];
        for (int j=0; j<v.size();j++){
            pred[j]=((Integer) v.elementAt(j)).intValue();
        }
        System.out.println("Predecessors of "+(i+1));
        System.out.println(Utils.toStringPlusOne(pred));
        return pred;
    }
    
    double arg(int i){
        if (i==nbpoints){return 0;}
        double x=P[i].getX()-P[nbpoints-1].getX();
        double y=P[i].getY()-P[nbpoints-1].getY();
        return Math.atan2(-y,x);
    }
    
    public void colorStables(){
        int i,j;
        int[] p;
        double a;
        boolean isstable;
        for (i=0; i<nbpoints-1;i++){
            //System.out.println("Color stables: i="+i);
            if (i<submodules.size()){
                p=(int[]) submodules.elementAt(i);
                isstable=true;
                a=arg(i);
                for (j=0;j<p.length; j++){
                    if (arg(p[j])>a){isstable=false;}
                }
                if (!isstable){
                    P[i].setColor(Color.WHITE);
                }
                else {
                    P[i].setColor(Color.RED);
                }
            }
            else {
                //P[i].setColor(Color.PINK);
            }
        }
        for (i=0;i<simples.length;i++){
            P[simples[i]].setColor(Color.PINK);
        }
        P[nbpoints-1].setColor(Color.BLUE);
    }
    
    public BigInteger dimHom(int l, int m){
        if (dimVect==null){return null;}
        int meshlength=dimVect.nbcols;
        int l1=l/meshlength;
        int l2=l-l1*meshlength;
        int m1=m/meshlength;
        int m2=m-m1*meshlength;
        //System.out.println("l1, l2 : " + l1 + " , " + l2);
        //System.out.println("m1, m2 : "+ m1 + " , " + m2);
        if (m1<l1){
            return BigInteger.ZERO;
        }
        else {
            return dimVect.A[m-l1*meshlength][l2];
        }
    }
    
    public boolean HomLessEqual(int i, int j){
        if (dimVect==null){return false;}
        boolean le=true;
        for (int k=0; k<nbpoints;k++){
            if (dimHom(k,i).compareTo(dimHom(k,j))>0)
            {le=false;}
        }
        return le;
    }
    
    synchronized void relax(int edgelen) {
        
        boolean boundexists=false;
        for (int i=0; i<nbpoints; i++){
            for (int j=i+1; j<nbpoints; j++){
                if (CM==null){
                    boundexists=(M.A[i][j].compareTo(BigInteger.ZERO)!=0);
                }
                else {
                    boundexists=CM.existArrows(i,j);
                }
                if (boundexists){
                    double vx=P[j].getX()-P[i].getX();
                    double vy=P[j].getY()-P[i].getY();
                    double len = Math.sqrt(vx * vx + vy * vy);
                    len = (len == 0) ? .0001 : len;
                    double f = (edgelen - len) / (len * 3);
                    double dx = f * vx;
                    double dy = f * vy;
                    P[j].dx+=dx;
                    P[j].dy+=dy;
                    P[i].dx+=-dx;
                    P[i].dy+=-dy;
                }
            }
        }
        
       
        for (int i=0; i<nbpoints; i++){
            MoveablePoint P1=P[i];
            double dx=0;
            double dy=0;
            
            for (int j=0; j<nbpoints; j++){
                if (i==j){
                    continue;
                }
                MoveablePoint P2=P[j];
                double vx=P1.getX()-P2.getX();
                double vy=P1.getY()-P2.getY();
                double len=vx*vx+vy*vy;

                if (len == 0) {
                        dx += Math.random();
                        dy += Math.random();
                }
                else if (len < 100*100) {
                    dx += vx / len;
                    dy += vy / len;
                }
            }
	    double dlen = dx * dx + dy * dy;
	    if (dlen > 0) {
		dlen = Math.sqrt(dlen) / 2;
                P1.dx += dx / dlen;
		P1.dy += dy / dlen;
	    }
	}
        
        for (int i=0; i<nbpoints; i++){
            if (P[i].fixed){
                continue;
            }
            if (P[i].frozen){
                continue;
            }
            MoveablePoint P1=P[i];
            double x= P1.getX()+Math.max(-5, Math.min(5,P1.dx));
            double y= P1.getY()+Math.max(-5, Math.min(5,P1.dy));
            P1.setLocation(x,y);
            Dimension d=qd.getSize();
            if (P1.x < 0) {
                P1.x = 0;
            } else if (P1.x > d.width) {
                P1.x = d.width;
            }
            if (P1.y < 0) {
                P1.y = 0;
            } else if (P1.y > d.height) {
                P1.y = d.height;
            }
            P1.dx/=2;
            P1.dy/=2;
        }
    }
    
    public void permuteVertices(int[] perm){
        Hist.permute(Seed.invPerm(perm));
        //System.out.println(M.toString());
        M.permuteRowsCols(perm,perm);
        //System.out.println(M.toString());
        /*MoveablePoint[] Q=new MoveablePoint[nbpoints];
        for (int i=0; i<nbpoints; i++){
            Q[i]=P[perm[i]];
        }
        P=null;
        P=Q;
         */
    }

    public void permuteVertexPositions(int[] perm){
        MoveablePoint[] Q=new MoveablePoint[nbpoints];
        for (int i=0; i<nbpoints; i++){
            Q[i]=P[perm[i]];
        }
        P=null;
        P=Q;
    }
    
    public void setClusterdim(int cd){
        CM=new CMatrix(cd, M);
        System.out.println(CM);
    }
    
    public int getClusterdim(){
        if (CM==null){return 2;}
        return CM.getClusterdim();
    }
    
    public String qysystem(){
        return M.qysystem();
    }
    
    public int getArrowStyle(int i,int j){
        if (StyleMatrix==null){return 0;}
        return StyleMatrix.getentry(i, j);
    }
    
    public void setArrowStyle(int i,int j, int st){
        if (StyleMatrix==null){
            StyleMatrix = new SMatrix(nbpoints, nbpoints);
        }
        StyleMatrix.setentry(i,j,st);
        StyleMatrix.setentry(j, i, st);
    }
    
    public BMatrix MatrixWithoutDashedArrows(){
        BMatrix Mret=new BMatrix(M);
        if (StyleMatrix==null){return Mret;}
        
        for (int i=0;i<nbpoints;i++){
            for (int j=0;j<nbpoints;j++){
                if (StyleMatrix.M[i][j]!=0){
                    Mret.A[i][j]=BigInteger.ZERO;
                    Mret.A[j][i]=BigInteger.ZERO;
                }
            }
        }

        return Mret;
    }
    
    public BMatrix MatrixOfDashedArrows(){
        BMatrix Mret=new BMatrix(M);
        if (StyleMatrix==null){
            Mret.makeZero();
        }
        else {
            for (int i=0;i<nbpoints;i++){
                for (int j=0;j<nbpoints;j++){
                    if (StyleMatrix.M[i][j]==0){
                        Mret.A[i][j]=BigInteger.ZERO;
                        Mret.A[j][i]=BigInteger.ZERO;
                    }
                }
            }
        }
        return Mret;
    }
    
    public BMatrix getLambda(){
        return Lambda;
    }
    
    public void setLambda(BMatrix argLam){
        Lambda=new BMatrix(argLam);
    }
    
    public BMatrix BtransposeLambda(){
        if (Lambda==null){return null;}
        BMatrix U=new BMatrix(M);
        U.transpose();
        U.multiplyby(Lambda);
        return U;
    }
    
    public boolean checkIfBandLambdaAreCompatible(){
        if (Lambda==null){return false;}
        BMatrix U=new BMatrix(M);
        U.transpose();
        U.multiplyby(Lambda);
        boolean compat=true;
        for (int i=0;i<U.nbrows;i++){
            for (int j=0;j<U.nbcols;j++){
                if (i!=j){
                    if (U.A[i][j].compareTo(BigInteger.ZERO)!=0){
                        compat=false;
                    }
                }
                if (i==j){
                    if (U.A[i][j].compareTo(BigInteger.ZERO)<0){
                        compat=false;
                    }
                }
            }
        }
        return compat;
    }
    
    
    public BMatrix initializeLambda(BMatrix Marg){
        
        BMatrix B=new BMatrix(Marg);
        
        if (B.nbrows==2){
            B.A[0][1]=BigInteger.valueOf(B.A[0][1].signum());
            B.A[1][0]=B.A[0][1].negate();
            B.A[0][0]=BigInteger.ZERO;
            B.A[1][1]=BigInteger.ZERO;
            Lambda=B;
            return B;
        }
        
        B.transpose();
        
        if (!B.isAntisymmetric()){
           BMatrix D=Utils.antisymmetrizingDiag(B);
           if (D==null){return null;}   
           B.leftmultiplyby(D);
        }
        
           
        try{
            B=B.adjoint();
        }
        catch (Exception e){
	    System.out.println(e.getMessage());
            return null;
	}
        
        if (B!=null){
            BigInteger d=B.gcd();
            B.divideElementsBy(d);
            Lambda=B;
        }
        return B;
    }
    
    public float getgrowthFactor(){
        return growthFactor;
    }
    
    public void setgrowthFactor(float f){
        growthFactor=f;
    }
        
    public void oppose(){
        M.transpose();
    }
    
    public Boolean invert(){
        BMatrix Mloc=new BMatrix(M);
        try{
            Mloc.invert();
        }
        catch (Exception e){
	    System.out.println(e.getMessage());
            return null;
	}
        M=Mloc;
        return Boolean.TRUE;
    }
    
    public void addarrow(int i, int j){
	//System.out.println("i:"+i+" j:"+j);
	if(i!=j){
	    BigInteger un=new BigInteger("1");
	    BigInteger moinsun=new BigInteger("-1");
	    BigInteger t1=M.A[i][j];
	    M.A[i][j]=t1.add(un);
	    BigInteger t2=M.A[j][i];
	    M.A[j][i]=t2.add(moinsun);
	}
    }
    
    public void mergevertices(int i1, int i2){
        int i3=nbpoints-1;
        for (int j=0; j<nbpoints-1; j++){
            M.A[i3][j]=M.A[i1][j].add(M.A[i2][j]);
            M.A[j][i3]=M.A[j][i1].add(M.A[j][i2]);
        }
    }
    
    public void showHeights(){
        for (int i=0; i<nbpoints; i++){
            P[i].showHeight();
        }
    }

    public void hideHeights(){
        for (int i=0; i<nbpoints; i++){
            P[i].label="";
        }
    }
    
   
    public void addvaluedarrow(int i, int j, int v1, int v2){
	//System.out.println("i:"+i+" j:"+j);
	if(i!=j){
            BigInteger t1=M.A[i][j];
            M.A[i][j]=new BigInteger(""+v1);
            BigInteger t2=M.A[j][i];
            M.A[j][i]=new BigInteger(-v2+"");
	}
    }

    public void addnode(int x, int y){
    BMatrix newM;
    int newnbpoints;
    int i,j;
    MoveablePoint[] newP;

    //System.out.println("New point at " + x+ " " + y);
    newnbpoints=nbpoints+1;

    newP=new MoveablePoint[newnbpoints];
        for (i=0;i<nbpoints;i++){newP[i]=P[i];}
        newP[newnbpoints-1]= new MoveablePoint(x,y, vertexradius);

    newM=new BMatrix(M.nbrows+1,M.nbcols+1);
        newM.copyfrom(M);
	for (i=0;i<newM.nbrows;i++){
	    newM.A[i][newM.nbcols-1] = new BigInteger("0");}
	for (j=0;j<newM.nbcols-1;j++){
	    newM.A[newM.nbrows-1][j] = new BigInteger("0");}
        
        nbpoints=newnbpoints;

        M=null; 
    P=null;
    M=newM;
    Lambda=null;
    P=newP;
    }
 
    public void initializeWordquiver(){
        blueprintnbpoints=nbpoints;
        word= new Vector(10);
        taumutseq=null;
        tauperm=null;
        taufirstoccur=null;
    }
    
    
    
    public String getWordquiverTaumutseq(){
        return taumutseq;
    }
    
    public String getWordquiverTauperm(){
        return tauperm;
    }

    public int[] getWordquiverTaufirstoccur(){
        return taufirstoccur;
    }
    
    public void finalizeWordquiver(){
        String str="";
        String heads="";
        int[] perm=new int[word.size()];
        int[] list=new int[word.size()];
        taufirstoccur=new int[blueprintnbpoints];
        for (int i=0; i<blueprintnbpoints; i++){
            taufirstoccur[i]=-1;
        }

        int cter,cter1,k,l;
        for (int i=0; i<word.size(); i++){
            k=((Integer) word.get(i)).intValue();
            cter=0;
            for (int j=i; j<word.size(); j++){
                l=((Integer) word.get(j)).intValue();
                if (k==l){
                    list[cter]=j;
                    cter++;
                }
            }

            boolean isfirstoccurrence=true;
            int wj;
            for (int j=0; j<i;j++){
               wj=((Integer) word.get(j)).intValue();
               if (k==wj){isfirstoccurrence=false;}
            }

            if (isfirstoccurrence){
                taufirstoccur[k]=i+blueprintnbpoints;
                //System.out.println("Position: "+ i+ ", letter: "+k + ", firstoccur: "+taufirstoccur[k]);
                //System.out.println("List: "+Utils.toStringPlusOne(list));
                for (int j=0; j<cter; j++){
                    perm[list[j]]=list[cter-j-1];
                }
            }
            
            
            cter1=0;
            for (int j=0; j<word.size(); j++){
                l=((Integer) word.get(j)).intValue();
                if ((k==l) && (cter1<cter)){
                    if (str.length()>0){
                        str=str+" "; 
                    }
                    str=str+(blueprintnbpoints+j+1);
                    if (cter1==0){
                        if (heads.length()>0){
                            heads=heads+" ";
                        }
                        heads=heads+(blueprintnbpoints+j+1);
                    }
                    cter1++;
                }
            }
            System.out.println(str);
            System.out.println(heads);
        }
        taumutseq=str;

        int[] shiftperm=new int[blueprintnbpoints+perm.length];
        for (int j=0; j<blueprintnbpoints+perm.length;j++){
            if (j<blueprintnbpoints){
                shiftperm[j]=j;
            }
            else {  
                shiftperm[j]=blueprintnbpoints+perm[j-blueprintnbpoints];
            }
        }
        tauperm=Utils.toStringPlusOne(shiftperm);

        //System.out.println(taumutseq);
        //System.out.println(tauperm);

        int[] firstoccur=getWordquiverTaufirstoccur();
             for (int i=0; i<blueprintnbpoints;i++){
                //S.deletepoint(0);
                P[i].togglePhase();
                if (firstoccur[i]>-1){
                    addarrow(i,firstoccur[i]);
                }
            }

    }
    
    
    
    public void addwnode(int x, int y){
    BMatrix newM;
    int newnbpoints;
    int i,j,type;
    MoveablePoint[] newP;


    type=-1;
    for (i=0;i<blueprintnbpoints;i++){
        if (Math.abs(P[i].y-y)<10){
            type=i;
        }
    }
    //System.out.println("Type="+(type+1));

    for (i=0; i<word.size();i++){
        j=((Integer) word.elementAt(i)).intValue()+1;
        //System.out.println("Word "+i+" :"+j);
    }


    if (type>-1){
            //System.out.println("New point at " + x+ " " + y);
            newnbpoints=nbpoints+1;

            newP=new MoveablePoint[newnbpoints];
                for (i=0;i<nbpoints;i++){newP[i]=P[i];}
                newP[newnbpoints-1]= new MoveablePoint(x,y, vertexradius);

            newM=new BMatrix(M.nbrows+1,M.nbcols+1);
                newM.copyfrom(M);
                for (i=0;i<newM.nbrows;i++){
                    newM.A[i][newM.nbcols-1] = new BigInteger("0");}
                for (j=0;j<newM.nbcols-1;j++){
                    newM.A[newM.nbrows-1][j] = new BigInteger("0");}

                nbpoints=newnbpoints;

                M=null;
            P=null;
            M=newM;
            Lambda=null;
            P=newP;

            for (i=0; i<word.size();i++){
                //System.out.println("Word "+i+" :"+word.elementAt(i));
            }

            j=word.lastIndexOf(new Integer(type));
            //System.out.println("Last occurence of "+(type+1)+" : "+j);
            if (j>=0){
                addarrow(nbpoints-1,j+blueprintnbpoints);
            }

            BigInteger m;
            for (j=0; j<blueprintnbpoints; j++){
              m=M.A[type][j].abs();
              if (m.compareTo(BigInteger.ZERO)!=0){
                  int s=word.lastIndexOf(new Integer(j));
                  if (s>=0){
                      int t=word.lastIndexOf(new Integer(type));
                      if ((t>=0)&& (t>s)){
                          M.A[blueprintnbpoints+s][blueprintnbpoints+t]=BigInteger.ZERO;
                          M.A[blueprintnbpoints+t][blueprintnbpoints+s]=BigInteger.ZERO;
                      }
                      M.A[blueprintnbpoints+s][nbpoints-1]=m;
                      M.A[nbpoints-1][blueprintnbpoints+s]=m.negate();
                  }
              }
            }
            word.add(new Integer(type));
        }
    }
    
    public void initializeDoubleWordquiver(){
        blueprintnbpoints=nbpoints;
        blueprintM=new BMatrix(M);
        word= new Vector(10);
        for (int i=0; i<nbpoints; i++){
            word.add(new Integer(-i-1));
            P[i].setColor(Color.GREEN);
        }
    }
    
    public void finalizeDoubleWordquiver(){
        int i;
        for (i=0; i<blueprintnbpoints;i++){
            P[i].setFrozen(true);
        }
        for (i=blueprintnbpoints; i<nbpoints; i++){
            if (nextsameabsvalue(i)>=nbpoints){
                P[i].setFrozen(true);
            }
        }
    }
    
    public int nextsameabsvalue(int k){
        int ik=((Integer) word.elementAt(k)).intValue();
        int absik=Math.abs(ik);
        int l=k+1;
        if (l>=word.size()){
            return word.size();
        }
        
        int absil=Math.abs(((Integer) word.elementAt(l)).intValue());
        while ((absil!=absik)){
            l=l+1;
            if (l>=word.size()){
                return word.size();
            }
            absil=Math.abs(((Integer) word.elementAt(l)).intValue());
        }
        return l;
    }
    
    public void addDoubleWNode(int x, int y, boolean altdown){
        BMatrix newM;
        int newnbpoints;
        int i,j,type;
        MoveablePoint[] newP;


        type=0;
        boolean found=false;
        for (i=0;i<blueprintnbpoints;i++){
            if (Math.abs(P[i].y-y)<10){
                type=i;
                found=true;
            }
        }

        if (!found){return;}

        type=type+1;
        if (altdown){type=-type;}
        System.out.println("Type="+type);

        word.add(new Integer(type));

        for (i=0; i<word.size();i++){
            j=((Integer) word.elementAt(i)).intValue();
            System.out.println("Word "+i+" :"+j);
        }

        newnbpoints=nbpoints+1;

        newP=new MoveablePoint[newnbpoints];
            for (i=0;i<nbpoints;i++){newP[i]=P[i];}
            newP[newnbpoints-1]= new MoveablePoint(x,y, vertexradius);
            if (altdown){
                newP[newnbpoints-1].setColor(Color.GREEN);
            }

        newM=new BMatrix(M.nbrows+1,M.nbcols+1);
        newM.makeZero();
        nbpoints=newnbpoints;

        M=null;
        P=null;
        
        M=newM;
        Lambda=null;
        P=newP;

        BigInteger m;
        for (int k=0; k<word.size();k++){
            int ik=((Integer) word.elementAt(k)).intValue();
            int kplus=nextsameabsvalue(k);
            System.out.println("k, ik, kplus:"+k+", "+ik+", "+kplus);
            int lplus=0;
            int il=0;
            for (int l=k+1; l<word.size(); l++){
                il=((Integer) word.elementAt(l)).intValue();
                lplus=nextsameabsvalue(l);
                System.out.println("l, il, lplus:"+l+", "+il+", "+lplus);
                if (l==kplus){
                    m=BigInteger.ONE;
                    if (il>0){
                        M.A[k][l]=m;
                        M.A[l][k]=m.negate();
                    }
                    else {
                        M.A[k][l]=m.negate();
                        M.A[l][k]=m;
                    }
                }
                int ikp=0;
                if (kplus<word.size()){
                    ikp=((Integer) word.elementAt(kplus)).intValue();
                }
                int ilp=0;
                if (lplus<word.size()){
                    ilp=((Integer) word.elementAt(lplus)).intValue();
                }

                System.out.println("abs(ik)-1="+(Math.abs(ik)-1));
                System.out.println("abs(il)-1="+(Math.abs(il)-1));
                
                m=blueprintM.A[Math.abs(ik)-1][Math.abs(il)-1];
                m=m.abs();
                System.out.println("Blueprint abs="+m.toString());

                if (m.compareTo(BigInteger.ZERO)!=0){
                    if (il>0){
                        m=m.negate();
                    }
                    if ((l<kplus)&&(kplus<lplus)&&(il*ikp>0)){
                        M.A[k][l]=m;
                        M.A[l][k]=m.negate();
                    }
                    if ((l<lplus)&&(lplus<kplus)&&(il*ilp<0)){
                        M.A[k][l]=m;
                        M.A[l][k]=m.negate();
                    }
                }
            }
        }
    }
    
    


  private int nb(int k, int m){return (k*(k+1)/2+m);}
  
  public Quiver(int choice, int param, QuiverDrawing qd){
        int i,j,k,n,m;
        String patternstr, str;
        String[] fields;
    
        this.qd=qd;
        quiverType=choice;
        parameter=param;
        quiverFilename="";
        vertexradius=9;
        showLabels=true;
        showFrozenVertices=true;
        trafficLights=false;
        shortNumbers=false;
        growthFactor=(float) 0.2;
        arrowlabelsize=12;
        
        Oxvector=null;
        RCharges=null;
        
        dimVect=null;
        simples=null;
        showSpikes=false;

        tauorder=-1;

        
        //Rectangle r=jp.getBounds();
        //int rw=r.width;
        //int rh=r.height;
        //if (rw==0){rw=350;}
        //if (rh==0){rh=300;}
        //int xoffset=rw/2-30*(param-1);
        //int yoffset=rh/2-25*(param-1);
        
        //int xoffset=r.width;
        //int yoffset=r.height;
        //System.out.println(r);
        //System.out.println("xoffset "+xoffset+" yoffset " +yoffset);
        
        
        nbpoints=nb(param-1, param-1)+1;
        //System.out.println("nbpoints: "+nbpoints);
        M=new BMatrix(nbpoints,nbpoints);
        Lambda=null;
        CM=null;
        
        
        P=new MoveablePoint[nbpoints];
        
        String s="x0";
        for (i=1;i<nbpoints;i++){
            s=s+",x"+i;
        }
        
        SeqDia=null;
        Hist=new History(qd);
       
        n=param;
        for (i=0; i<n; i++){
            for (j=0; j<=i; j++){
                k=nb(i,j);
                //System.out.println("i:"+i+" j:"+j+" k:"+k);
                P[k]=new MoveablePoint(30*(n-i-1+2*j), 50*i);
                
                if ((j <= i-1) && (i >=1)){
                    //System.out.println("k="+k+" nb("+(i-1)+","+j+")="+nb(i-1,j));
                    m=nb(i-1,j);
                    M.A[k][m]=new BigInteger("1"); 
		    M.A[m][k]=new BigInteger("-1");}
                if(j-1>=0){
                    //System.out.println("k="+k+" nb("+i+","+j+")="+nb(i,j-1));
                    m=nb(i,j-1);
                    M.A[k][m]=new BigInteger("1");
		    M.A[m][k]=new BigInteger("-1");}
                if(i+1<n){
                    //System.out.println("k="+k+" nb("+(i+1)+","+(j+1)+")="+nb(i+1,j+1));
                    m=nb(i+1,j+1); 
                    M.A[k][m]=new BigInteger("1");
		    M.A[m][k]=new BigInteger("-1");}
            }
        }
	for (i=0; i<nbpoints; i++){
	    for (j=0; j<nbpoints; j++){
		if (M.A[i][j]==null){M.A[i][j]=new BigInteger("0");}
	    }
	}
	// System.out.println(M);
        Rectangle r=qd.getBounds();
        if (r.getWidth()==0){r.setRect(0,0,350,300);}
        //if (rw==0){rw=350;}
        //if (rh==0){rh=300;}
        //System.out.println("jp rectangle "+r);
        //System.out.println("enclosing rectangle "+enclosingRectangle());
        scalecenter(r);
  }
  
  public Quiver(AbstractQuiver argaq, QuiverDrawing qd){
      this(argaq.toMatrix(), qd);
      for (int i=0; i<nbpoints; i++){
          P[i].frozen=argaq.Vertices[i].frozen;
      }
  }
 
  
  public Quiver(BMatrix argM, QuiverDrawing qd){
        int i;
        String patternstr, str;
        String[] fields;
    
        this.qd=qd;
        quiverType=0;
        parameter=4;
        quiverFilename="";
        vertexradius=9;
        showLabels=true;
        shortNumbers=false;
        showFrozenVertices=true;
        growthFactor=(float) 0.2;
        arrowlabelsize=12;
        
        Oxvector=null;
        RCharges=null;
        
        dimVect=null;
        simples=null;
        showSpikes=false;
        

        tauorder=-1;
        
        nbpoints=argM.nbrows;
        //System.out.println("nbpoints: "+nbpoints);
        M=new BMatrix(argM.nbrows, argM.nbcols);
	M.copyfrom(argM);
        Lambda=null;
        CM=null;
        
        SeqDia=null;
        Hist=new History(qd);
        
        P=new MoveablePoint[nbpoints];
        
        String s="x0";
        for (i=1;i<nbpoints;i++){
            s=s+",x"+i;
        }
         
        float R=60;
        int x=0;
        int y=0;
        float delta=(float) (2*Math.PI/nbpoints);
        for (i=0; i<nbpoints; i++){
            x=(int) Math.round(R*Math.cos(i*delta));
            y=-(int) Math.round(R*Math.sin(i*delta));
            P[i]=new MoveablePoint(x,y);
        }
        
        Rectangle r=qd.getBounds();
        if (r.getWidth()==0){r.setRect(0,0,350,300);}
        //if (rw==0){rw=350;}
        //if (rh==0){rh=300;}
        //System.out.println("jp rectangle "+r);
        //System.out.println("enclosing rectangle "+enclosingRectangle());
        scalecenter(r);
  }

  public Quiver copy(){
      Quiver copiedquiver=new Quiver(M,qd);
      for (int i=0; i<nbpoints; i++){
          copiedquiver.P[i]=(MoveablePoint) P[i].clone();
      }
      if (StyleMatrix!=null){
        copiedquiver.StyleMatrix=new SMatrix(StyleMatrix.M);
      }
      copiedquiver.growthFactor=growthFactor;
      return copiedquiver;
  }
  
  public BMatrix repeat(int nber){
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        C.copyfrom(B);
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
              P[nbpoints-1].setFrozen(P[q].frozen);
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              for (j=0;j<orignbpoints;j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p-1)*orignbpoints][j].negate();
              }
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p-1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      return C;
  }
  
  
   public RingElt[] repeatwithcluster(int nber, boolean numeric, String initialValues){
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        C.copyfrom(B);
        

        String st;
        
        RingElt[] X;
        Ring R;
        X=null;
        R=null;
        st="";
        if (!numeric){
            for (i=1;i<=orignbpoints;i++){
            st=st+",x"+i;
            }
        }
        
        //System.out.println(s);
        R=new QuotientField(new PolynomialRing(Ring.Z, st));
        X=new RingElt[nber*orignbpoints];
        for (i=0; i<orignbpoints; i++){
            if (numeric) {
                String patternstr=" ";
                String[] fields=initialValues.split(patternstr);
                for (i=0; i<orignbpoints; i++){
                    X[i]=R.map(fields[i]);
                } 
            }
            else {
            X[i]=R.map("x"+(i+1));
            //System.out.println(X[i]);
            }
        } 
                
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
              
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              for (j=0;j<orignbpoints;j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p-1)*orignbpoints][j].negate();
              }
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p-1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      
      
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              RingElt P=R.one();
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      P=R.mult(P,R.pow(X[i+p*orignbpoints],c));
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      P=R.mult(P,R.pow(X[i+(p-1)*orignbpoints],c));
                  }
              }
              P=R.add(R.one(),P);
              System.out.println("About to divide in step "+ (1+q+p*orignbpoints)+":\n"+"Numerator:"+P+"\nDenominator:"+X[q+(p-1)*orignbpoints]);
              X[q+p*orignbpoints]=R.div(P,X[q+(p-1)*orignbpoints]);
              System.out.println("Division successful.");
              //System.out.println(X[q+p*orignbpoints]);
            }
      }
      
      return X;
  }
   
  public BigInteger[] repeatwithtropcluster(int nber, String initialValues){
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        C.copyfrom(B);
        

        String patternstr=" ";
        String[] fields=initialValues.split(patternstr);
        
        BigInteger[] X=new BigInteger[nber*orignbpoints];
        BigInteger[] XR=new BigInteger[nber*orignbpoints];
        for (i=0; i<orignbpoints; i++){
                X[i]=new BigInteger(fields[i]);
                XR[i]=new BigInteger(fields[i]);
        } 
              
        
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              for (j=0;j<orignbpoints;j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p-1)*orignbpoints][j].negate();
              }
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p-1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      
      BigInteger alpha, alphaR;

      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              BigInteger P=BigInteger.ZERO;
              BigInteger PR=BigInteger.ZERO;
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      alpha=X[i+p*orignbpoints].multiply(c);
                      alphaR=XR[i+p*orignbpoints].multiply(c);
                      P=P.add(alpha);
                      if (alphaR.compareTo(BigInteger.ZERO)>=0){
                          PR=PR.add(alphaR);
                      }
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      alpha=X[i+(p-1)*orignbpoints].multiply(c);
                      alphaR=XR[i+(p-1)*orignbpoints].multiply(c);
                      P=P.add(alpha);
                      if (alphaR.compareTo(BigInteger.ZERO)>=0){
                          PR=PR.add(alphaR);
                      }
                  }
              }
              P=P.max(BigInteger.ZERO);
              X[q+p*orignbpoints]=P.subtract(X[q+(p-1)*orignbpoints]);
              XR[q+p*orignbpoints]=PR.subtract(XR[q+(p-1)*orignbpoints]);
              //System.out.println(X[q+p*orignbpoints]);
            }
      }
      
      // BigInteger c;
      //int i;

      /* BigInteger tauc;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
                i=p*orignbpoints+q;
                c=X[i];
                tauc=X[(p-1)*orignbpoints+q].negate();
                P[i].setContent(c.toString());
                System.out.println("p="+p+" q="+q+" i="+ i+" c="+c+" tauc="+tauc);
                if (c.compareTo(tauc)==0){
                    P[i].setColor(Color.GREEN);
                }
                else {
                    P[i].setColor(Color.RED);
                }
            }
      }
       */

      for (p=0; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
                i=p*orignbpoints+q;
                c=X[i];
                P[i].setContent(c.toString());
                if (c.compareTo(BigInteger.ZERO)>0){
                    P[i].setColor(Color.RED);
                }
                else {
                    P[i].setColor(Color.GREEN);
                }
            }
      }

      for (q=0;q<orignbpoints;q++){
          P[q].setContent(X[q].toString());
      }
      
      return X;
  }

  public BigInteger[] repeatwithclusteradd(int nber, String initialValues){
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;

      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }

      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());

      deletenode(nbpoints-1);
      orignbpoints=nbpoints;

        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        C.copyfrom(B);


        String patternstr=" ";
        String[] fields=initialValues.split(patternstr);

        BigInteger[] X=new BigInteger[nber*orignbpoints];
        BigInteger[] XR=new BigInteger[nber*orignbpoints];
        for (i=0; i<orignbpoints; i++){
                X[i]=new BigInteger(fields[i]);
                XR[i]=new BigInteger(fields[i]);
        }



      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }

      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              for (j=0;j<orignbpoints;j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p-1)*orignbpoints][j].negate();
              }
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p-1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);

      BigInteger alpha, alphaR;

      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              BigInteger P=BigInteger.ZERO;
              BigInteger PR=BigInteger.ZERO;
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      alpha=X[i+p*orignbpoints].multiply(c);
                      alphaR=XR[i+p*orignbpoints].multiply(c);
                      P=P.add(alpha);
                      if (alphaR.compareTo(BigInteger.ZERO)>=0){
                          PR=PR.add(alphaR);
                      }
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      alpha=X[i+(p-1)*orignbpoints].multiply(c);
                      alphaR=XR[i+(p-1)*orignbpoints].multiply(c);
                      P=P.add(alpha);
                      if (alphaR.compareTo(BigInteger.ZERO)>=0){
                          PR=PR.add(alphaR);
                      }
                  }
              }
              P=P.max(BigInteger.ZERO);
              X[q+p*orignbpoints]=P.subtract(X[q+(p-1)*orignbpoints]);
              XR[q+p*orignbpoints]=PR.subtract(XR[q+(p-1)*orignbpoints]);
              //System.out.println(X[q+p*orignbpoints]);
            }
      }

      // BigInteger c;
      //int i;
      for (p=0; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
                i=p*orignbpoints+q;
                c=XR[i];
                P[i].setContent(c.toString());
                if (c.compareTo(BigInteger.ZERO)>0){
                    P[i].setColor(Color.RED);
                }
                else {
                    P[i].setColor(Color.GREEN);
                }
            }
      }

      return XR;
  }
  
  public BMatrix repeatwithweight(int nber){
     
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
      
      /*
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      */
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
      BMatrix origMatrix=new BMatrix(M.A);
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
      
      for (r=0; r<nber-1; r++){
          for (s=0; s<orignbpoints; s++){
              addarrow(s+(r+1)*orignbpoints,s+r*orignbpoints);
          }
      }
      
      int N=nber*orignbpoints;
      
      BMatrix AUO= new BMatrix(N,N);
      AUO.makeZero();
      for (r=0;r<orignbpoints;r++){
          for (s=0;s<nber; s++){
              for (int t=0;t<=s;t++){
                  AUO.A[r+s*orignbpoints][r+t*orignbpoints]=BigInteger.ONE;
              }
          }
      }
      return AUO;
  }
  
   
  public BMatrix repeatwithdimright(int nber){
     
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        C.copyfrom(B);
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
              
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
      for (i=0; i<orignbpoints; i++){
            P[i].setFrozen(true);
        }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=1; p<nber; p++){
            for (q=0;q<orignbpoints;q++){
              for (j=0;j<orignbpoints;j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p-1)*orignbpoints][j].negate();
              }
              for (i=0;i<orignbpoints; i++){
                  c=M.A[i+p*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[i+(p-1)*orignbpoints][q+p*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p-1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      
      int N=nber*orignbpoints;
      
      BMatrix AUO= new BMatrix(N,N);
      for (r=0; r<nber; r++){
      for (s=0; s<orignbpoints; s++){
          for (p=0; p<nber; p++){
          for (q=0; q<orignbpoints; q++){
              BigInteger Sum=BigInteger.ZERO;
              for (int t=0; t<=p; t++){
                  if (0<=r-p+t){
                  Sum=Sum.add(C.A[s+(r-p+t)*orignbpoints][q]);
                  }
              }
              AUO.A[s+r*orignbpoints][q+p*orignbpoints]=Sum;
          }
          }
      }
      }
      
      //System.out.println("Auslander orbit matrix");
      //System.out.println(AUO);
      
      for (r=0; r<nber-1; r++){
      for (s=0; s<orignbpoints; s++){
          addarrow(s+(r+1)*orignbpoints,s+r*orignbpoints);
      }
      }
      
      return AUO;
  }
  
   public BMatrix repeatwithdimleft(int nber){
      
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        //A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        for (i=n*nber-orignbpoints; i<n*nber; i++){
            for (j=0; j<orignbpoints; j++){
                C.A[i][j]=B.A[i-n*nber+orignbpoints][j];
            }
        }
        //System.out.println("Initial matrix");
        //System.out.println(C);
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
              
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
      for (i=0; i<orignbpoints; i++){
            P[i+(nber-1)*orignbpoints].setFrozen(true);
        }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=nber-2; p>=0; p--){
            for (q=orignbpoints-1;q>=0;q--){
              for (j=0; j<orignbpoints; j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p+1)*orignbpoints][j].negate();
              }
              for (i=orignbpoints-1;i>=0; i--){
                  c=M.A[q+p*orignbpoints][i+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[q+p*orignbpoints][i+(p+1)*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p+1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      
      int N=nber*orignbpoints;
      
      BMatrix AUO= new BMatrix(N,N);
      for (p=0; p<nber; p++){
      for (q=0; q<orignbpoints; q++){
          for (r=0; r<nber; r++){
          for (s=0; s<orignbpoints; s++){
              BigInteger Sum=BigInteger.ZERO;
              for (int t=0; t<=p; t++){
                  if (p-t<=r){
                    Sum=Sum.add(C.A[q+(p+nber-1-r-t)*orignbpoints][s]);}
              }
              //System.out.println("(p,q)="+p+","+q+" (r,s)="+r+","+s+" Paths:"+Sum);
              AUO.A[q+p*orignbpoints][s+r*orignbpoints]=Sum;
          }
          }
      }
      }
      
      //System.out.println("Auslander orbit matrix");
      //System.out.println(AUO);
      
      for (r=0; r<nber-1; r++){
      for (s=0; s<orignbpoints; s++){
          addarrow(s+(r+1)*orignbpoints,s+r*orignbpoints);
      }
      }
      
      return AUO;
  }
   
    public BMatrix repeatwithDeltaleft(int nber, BigInteger[] h){
      
      int i,j, tx,ty, x,y,p,q,r,s, orignbpoints;
    
      for (i=0; i<nbpoints; i++){
          for (j=i; j<nbpoints; j++){
              if (M.A[i][j].signum()<0){
                  JOptionPane.showMessageDialog(qd,"There is an arrow from "+(j+1)+" to "+(i+1)+". \n"+
                          "Please renumber the nodes proceeding from sources to sinks.");
                  return null;
              }
          }
      }
      
      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());
      
      deletenode(nbpoints-1);
      orignbpoints=nbpoints;
      
        int n=M.nbrows;
        BMatrix A=new BMatrix(n,n);
        A.copyPosPartfrom(M);
        //A.transpose();
        //System.out.print(A);
        BMatrix B=new BMatrix(n,n);
        B.makeZero();
        B.addIdentity();
        //System.out.println();
        //System.out.print(B);
        for (i=0; i<n; i++){
            B.multiplyby(A);
            B.addIdentity();
        }
        //System.out.println();
        //System.out.print(B);
        BMatrix C=new BMatrix(n*nber,n);
        for (i=n*nber-orignbpoints; i<n*nber; i++){
            for (j=0; j<orignbpoints; j++){
                C.A[i][j]=B.A[i-n*nber+orignbpoints][j];
            }
        }
        //System.out.println("Initial matrix");
        //System.out.println(C);
        
      for (p=1; p<nber; p++){
          for (q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+p*tx, y+q*ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(P[q].getColor());
              
          }
          for (i=0;i<orignbpoints; i++){
              for (j=i+1;j<orignbpoints; j++){
                  if (M.A[i][j].signum()!=0){
                      M.A[i+p*orignbpoints][j+p*orignbpoints]=new BigInteger(M.A[i][j].toString());
                      M.A[j+p*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].toString());
                  }
              }
          }
          for (i=0;i<orignbpoints; i++){
              for (j=0; j<orignbpoints; j++){
                   if (M.A[i][j].signum()>0){
                       M.A[j+(p-1)*orignbpoints][i+p*orignbpoints]=new BigInteger(M.A[j][i].negate().toString());
                       M.A[i+p*orignbpoints][j+(p-1)*orignbpoints]=new BigInteger(M.A[i][j].negate().toString());
                   }
              }
          }
      }
        
        for (i=0; i<orignbpoints; i++){
            P[i+(nber-1)*orignbpoints].setFrozen(true);
        }
        
      //System.out.println();
      //System.out.println(C);
      //System.out.println(M);
      BigInteger c;
      for (p=nber-2; p>=0; p--){
            for (q=orignbpoints-1;q>=0;q--){
              for (j=0; j<orignbpoints; j++){
                  C.A[q+p*orignbpoints][j]=C.A[q+(p+1)*orignbpoints][j].negate();
              }
              for (i=orignbpoints-1;i>=0; i--){
                  c=M.A[q+p*orignbpoints][i+p*orignbpoints];
                  //System.out.println("from : "+i+p*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+p*orignbpoints, q+p*orignbpoints);
                  }
                  c=M.A[q+p*orignbpoints][i+(p+1)*orignbpoints];
                  //System.out.println("from : "+i+(p-1)*orignbpoints+" to "+q+p*orignbpoints+" : "+c);
                  if (c.signum()>0){
                      C.addmulrow(c, i+(p+1)*orignbpoints, q+p*orignbpoints);
                  }
              }
            }
      }
      //System.out.println("Dimension vectors:");
      //System.out.println(C);
      
      int N=nber*orignbpoints;
      
      BMatrix AUO= new BMatrix(N,N);
      
      for (p=0; p<nber; p++){
      for (q=0; q<orignbpoints; q++){
          for (r=0; r<nber; r++){
          for (s=0; s<orignbpoints; s++){
              AUO.A[q+p*orignbpoints][s+r*orignbpoints]=BigInteger.ZERO;
          }
          }
      }
      }
      
      for (p=0; p<nber; p++){
      for (q=0; q<orignbpoints; q++){
          for (r=0; r<=p; r++){
              AUO.A[q+p*orignbpoints][q+r*orignbpoints]=BigInteger.ONE;
          }
      }
      }
      
      
      for (p=0; p<nber; p++){
      for (q=0; q<orignbpoints; q++){
          BigInteger Sum=BigInteger.ZERO;
          for (r=0; r<nber; r++){
          for (s=0; s<orignbpoints; s++){
              if (p<=r){
                  Sum=Sum.add(C.A[q+(p+nber-1-r)*orignbpoints][s]);
              }
          }
          h[q+p*orignbpoints]=Sum;
          }
      }
      }
          
      //System.out.println("h-vector: ");
      for (p=0; p<nber; p++){
          for (q=0; q<orignbpoints; q++){
              //System.out.print(" "+h[q+p*orignbpoints]);
          }
          //System.out.println();
      }
      
      //System.out.println("Auslander orbit matrix");
      //System.out.println(AUO);
      
      for (r=0; r<nber-1; r++){
      for (s=0; s<orignbpoints; s++){
          addarrow(s+(r+1)*orignbpoints,s+r*orignbpoints);
      }
      }
      
      
      return AUO;
  }
  
        
   public void setvertexradius(int vr){
       for (int i=0;i<nbpoints;i++){P[i].setradius(vr);}
   }
   
   public void setarrowlabelsize(float sz){
       arrowlabelsize=sz;
   }
   
   public float getarrowlabelsize(){
       return arrowlabelsize;
   }
   
   public void setSeqDia(SequencesDialog argSeqDia){
       SeqDia=argSeqDia;
   }
   
   public SequencesDialog getSeqDia(){
       return SeqDia;
   }
   
   public boolean toggleSpikes(){
       showSpikes=!showSpikes;
       return showSpikes;
   }
   
   public MoveablePoint getNewPoint(){
       float x=P[nbpoints-1].x+10;
       float y=P[nbpoints-1].y;
       return new MoveablePoint(Math.round(x), Math.round(y));
   }
   
   public MoveablePoint getCenterOfGravity(){
       float sx=0;
       float sy=0;
       for (int i=0;i<nbpoints;i++){
           sx=sx+P[i].x;
           sy=sy+P[i].y;
       }
       return new MoveablePoint(Math.round(sx/nbpoints),Math.round(sy/nbpoints));
   }
   
   public int[] getFrozenVertices(){
       int i;
       int nbfv=0;
       for (i=0;i<nbpoints;i++){
           if (P[i].frozen){nbfv++;}
       }
       int[] fv=new int[nbfv];
       int cter=0;
       for (i=0; i<nbpoints;i++){
           if (P[i].frozen){
               fv[cter]=i; cter++;
           }
       }
       return fv;
   }
   
   private void computeAnglesBelow(int i, Double[] Angles){
       for (int j=0; j<nbpoints; j++){
           if ((M.A[i][j].compareTo(BigInteger.ZERO)!=0)&&(Angles[j]==null)){
               Angles[j]=new Double(Angles[i].doubleValue()+RCharges[i][j]);
               computeAnglesBelow(j, Angles);
           }
       }
   }
   
   public void computeAngles(){
       Double[] Angles=new Double[nbpoints];
       Angles[0]=new Double(0);
       computeAnglesBelow(0,Angles);
       double x, y;
       for (int i=0; i<nbpoints; i++){
           //System.out.println("Point "+ i+":"+ Oxvector[i]+" ,"+ Angles[i].doubleValue());
           x=Oxvector[i].doubleValue()*Math.cos(Math.PI*Angles[i].doubleValue());
           y=Oxvector[i].doubleValue()*Math.sin(Math.PI*Angles[i].doubleValue());
           P[i].setLocation(100*x, 100*y);
       }
   }
   
   
   public void setOxvector(String s){
       String[] fields=s.split(" ");
       if (fields.length!=nbpoints){
           Oxvector=null;
           System.out.println("Wrong number of components.");
           return;
       }
       Oxvector=new BigInteger[nbpoints];
       for (int i=0; i<nbpoints; i++){
           Oxvector[i]=new BigInteger(fields[i]);
       }
   }
   
   public String getOxvector(){
       String str="";
       if (Oxvector==null){
           return str;
       }
       for (int i=0; i<nbpoints; i++){
           str=str+" "+Oxvector[i];
       }
       return str;
   }
   
  public void setRCharges(String s){
       String[] fields=s.split(" ");
       int[] cycle=new int[fields.length];
       for (int i=0; i<fields.length; i++){
           cycle[i]=Integer.parseInt(fields[i])-1;
       }
       setRCharges(cycle);
  }
   
   public boolean setRCharges(int[] cycle){
       RCharges=new double[nbpoints][nbpoints];
       for (int i=0; i<nbpoints; i++){
           for (int j=0; j<nbpoints; j++){
               RCharges[i][j]=M.A[i][j].doubleValue()/(Oxvector[i].doubleValue()*Oxvector[j].doubleValue());
           }
       }
       double sum=RCharges[cycle[cycle.length-1]][cycle[0]];
       for (int i=0; i<cycle.length-1; i++){
           sum=sum+RCharges[cycle[i]][cycle[i+1]];
       }
       if (sum<0.0001){
           return false;
       }
       double f=2/sum;
       for (int i=0; i<nbpoints; i++){
           for (int j=0; j<nbpoints; j++){
               RCharges[i][j]=RCharges[i][j]*f;
           }
       }
       Jama.Matrix JM=new Jama.Matrix(RCharges);
       //JM.print(5,2);
       return true;
   }
   
   public int getvertexradius(){
       if (nbpoints>0){
           return P[0].getradius();
       }
       else return 0;
   }
 
   public void drawarrow(Point2D.Double P, Point2D.Double Q, double rho, Graphics2D g){
     
     double a,b,c,d, dist;
     AffineTransform T;
     Line2D line;
     Point2D P1, Q1, R;
     GeneralPath path;
     
     
     
   
   dist=P.distance(Q);
   Q1=new Point2D.Double();
   Q1.setLocation(Q.getX()+(rho/dist)*(P.getX()-Q.getX()),Q.getY()+(rho/dist)*(P.getY()-Q.getY()));
   P1=new Point2D.Double();
   P1.setLocation(P);
   line = new Line2D.Double();
   line.setLine(P1,Q1);
   g.draw(line);
   path=new GeneralPath();
   if (dist>50){
      P1.setLocation(Q1.getX()+(50/dist)*(P.getX()-Q1.getX()), Q1.getY()+(50/dist)*(P.getY()-Q1.getY()));
   }
         a=-P1.getX()+Q1.getX();
         b=-P1.getY()+Q1.getY();
         c=P1.getX();
         d=P1.getY();
         T=new AffineTransform(a,b,-b,a,c,d);
         R=new Point2D.Double(1-arrowheadlength,0.5*arrowheadwidth);
         T.transform(R, R);
         path.moveTo((float) R.getX(), (float) R.getY());
         R.setLocation(1-arrowheadlength,-0.5*arrowheadwidth);
         T.transform(R,R);
         path.lineTo((float) R.getX(),(float)R.getY());
         path.lineTo((float) Q1.getX(),(float) Q1.getY());
         path.closePath();
         g.fill(path);
   }
   
   public void addOrigin(){
       MoveablePoint Origin=getCenterOfGravity();
       addnode(Math.round(Origin.x),Math.round(Origin.y));
   } 
   
   public void addNewPoint(){
       MoveablePoint P=getNewPoint();
       addnode(Math.round(P.x),Math.round(P.y));
   }
   
   public void moveLeft(){
       Rectangle r=qd.getBounds();
        double x0=r.getX();
        double y0=r.getY();
        double width=r.getWidth();
        double height=r.getHeight();
        Rectangle r1=new Rectangle();
        double c=0.66;
        r1.setBounds((int) Math.round(x0), (int) Math.round(y0), (int) Math.round(c*width), (int) Math.round(height));
        horscalecenter(r1);
   }
   
   public void addQuiver(Quiver addQ){
        Rectangle r=qd.getBounds();
        double x0=r.getX();
        double y0=r.getY();
        double width=r.getWidth();
        double height=r.getHeight();
        Rectangle r1=new Rectangle();
        Rectangle r2=new Rectangle();
        double c=0.66;
        r1.setBounds((int) Math.round(x0), (int) Math.round(y0), (int) Math.round(c*width), (int) Math.round(height));
        r2.setBounds((int) Math.round(x0+c*width), (int) Math.round(y0), (int) Math.round((1-c)*width), (int) Math.round(height));
        scalecenter(r1);
        addQ.scalecenter(r2);
        
        System.out.println("Rectangles");
        System.out.println("r:"+r);
        System.out.println("r1:"+r1);
        System.out.println("r2:"+r2);
        
        
        BMatrix newM;
        int newnbpoints;
        int i,j;
        MoveablePoint[] newP;

        //System.out.println("New point at " + x+ " " + y);
        newnbpoints=nbpoints+addQ.nbpoints;
        newP=new MoveablePoint[newnbpoints];
            for (i=0;i<nbpoints;i++){newP[i]=P[i];}
            for (i=nbpoints;i<newnbpoints;i++){newP[i]=addQ.P[i-nbpoints];}
        
        for (i=0; i<newnbpoints;i++){
            System.out.println("New point "+i+": "+newP[i]);
        }
        
        M.appendBlock(addQ.M);
        System.out.println("New B-matrix"+M);
        
        P=newP;
        nbpoints=newnbpoints;
        
        Lambda=null;
}
   
   public void addFraming(){
   int tx, ty, x,y,orignbpoints;

      tx=40;
      ty=40;
      orignbpoints=nbpoints;


      for (int q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+tx, y+ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(Color.RED);
              P[nbpoints-1].frozen=true;
          }

      for (int q=0; q<orignbpoints; q++){
          M.A[q][q+orignbpoints]=BigInteger.ONE;
          M.A[q+orignbpoints][q]=BigInteger.ONE.negate();
      }
   }
   
   public void addExtendingVertices(){
   int tx, ty, x,y, orignbpoints;

      tx=(int) (P[nbpoints-1].getX()-P[nbpoints-2].getX());
      ty=(int) (P[nbpoints-1].getY()-P[nbpoints-2].getY());

      deletenode(nbpoints-1);
      orignbpoints=nbpoints;

      for (int q=0;q<orignbpoints;q++){
              x=(int) P[q].getX();
              y=(int) P[q].getY();
              addnode(x+tx, y+ty);
              P[nbpoints-1].setHeight(P[q].getHeight());
              P[nbpoints-1].setColor(Color.RED);
              P[nbpoints-1].frozen=true;
          }

      for (int q=0; q<orignbpoints; q++){
          M.A[q][q+orignbpoints]=BigInteger.ONE;
          M.A[q+orignbpoints][q]=BigInteger.ONE.negate();
      }
   }
   
   public void determineSubmodules(){
       submodules=new Vector(10);
       for (int i=0; i<nbpoints-1;i++){
           submodules.add(predec(i));
       }
   }
   
   public void enterSubmodules(Frame fr){
       submodules=new Vector(10);
       int[] p;
       for (int i=0; i<nbpoints; i++){
           String str=JOptionPane.showInputDialog(fr, "Enter the indec. submodules of " + (i+1)+" separated by spaces");
           if (str.length()>0){
               String[] fields=str.split(" ");
               p=new int[fields.length];
               for (int j=0; j<fields.length; j++){
                   p[j]=Integer.parseInt(fields[j])-1;
               }
               submodules.add(p);
           }
           else {
               p=new int[0];
               submodules.add(p);
           }
       }
   }
         
   public void determineSimples(boolean OriginPresent){

        simples=null;

        MoveablePoint Origin;
        if (OriginPresent){
            Origin=P[nbpoints-1];
            deletenode(nbpoints-1);
        }
        else {
            Origin=getCenterOfGravity();
            }

        MoveablePoint[] SimplePoint=new MoveablePoint[nbpoints];

        int n=dimVect.nbcols;
        BMatrix Proj=dimVect.extract(0,n,0,n);
        System.out.println("Proj:\n"+Proj.toString());
        try {
            Proj.invert();
        }
        catch (Exception e){
	    System.out.println(e.getMessage());
	}
        System.out.println("Proj:\n"+Proj.toString());

        double[] xp=new double[n];
        double[] yp=new double[n];
        for (int i=0; i<n; i++){
           xp[i]=P[i].getX()-Origin.getX();
           yp[i]=P[i].getY()-Origin.getY();
        }
        double x,y;
        for (int i=0; i<n; i++){
           x=Origin.getX(); y=Origin.getY();
           for (int j=0; j<n; j++){
               x=x+Proj.A[i][j].doubleValue()*xp[j];
               y=y+Proj.A[i][j].doubleValue()*yp[j];
           }
           SimplePoint[i]=new MoveablePoint((int) Math.round(x),(int) Math.round(y));
        }

        for (int i=0; i<n; i++){
            System.out.println("Simple "+i+" :"+ Math.round(SimplePoint[i].x)+", "+Math.round(SimplePoint[i].y));
        }


        simples=new int[n];
        BigInteger[] Si=new BigInteger[n];    
        boolean foundall=true;
        for (int i=0;i<n; i++){
            for (int j=0; j<n; j++){
                if (i==j) {Si[j]=BigInteger.ONE;} else {Si[j]=BigInteger.ZERO;}
            }
            boolean found=false;
            boolean equalsSi;
            for (int j=0; j<dimVect.nbrows; j++){
                equalsSi=true;
                for (int k=0;k<n;k++){
                    if (dimVect.A[j][k].compareTo(Si[k])!=0) {equalsSi=false;}
                }
                if (equalsSi){
                    simples[i]=j; 
                    found=true;
                }
            }
            if (!found){
                //JOptionPane.showMessageDialog(qd,"Could not find simple number "+(i+1));
                //foundall=false;
                addnode(Math.round(SimplePoint[i].x), Math.round(SimplePoint[i].y));
                dimVect.appendRow(Si);
                simples[i]=dimVect.nbrows-1;
                P[simples[i]].setColor(Color.GREEN);
            } 
            else {
                //System.out.println("Simple number "+(i+1)+" is "+(simples[i]+1));
                P[simples[i]].setColor(Color.GREEN);
            }
        }
        if (!foundall) {
            simples=null;
        }

        addnode(Math.round(Origin.x),Math.round(Origin.y));
        System.out.println("At end of determineSimples:\ndimVect:\n"+dimVect+"\nsimples:\n"+Utils.toString(simples));
    }
   
         
   public void recenter(){
       MoveablePoint Origin=P[nbpoints-1];
       int n=simples.length;
       double[] xs=new double[n];
       double[] ys=new double[n];
       for (int i=0; i<n; i++){
           xs[i]=P[simples[i]].getX()-Origin.getX();
           ys[i]=P[simples[i]].getY()-Origin.getY();
       }
       double x,y;
       for (int i=0; i<dimVect.nbrows; i++){
           x=Origin.getX(); y=Origin.getY();
           for (int j=0; j<n; j++){
               x=x+dimVect.A[i][j].doubleValue()*xs[j];
               y=y+dimVect.A[i][j].doubleValue()*ys[j];
           }
           P[i].setLocation(x,y);
       }
       //System.out.println("Origin:"+Origin.x+", "+Origin.y);
       //System.out.println("Last point:"+P[nbpoints-2].x+", "+P[nbpoints-2].y);
   }
   
   
   public BasicStroke getArrowStroke(int i, int j){
       BasicStroke stroke=new BasicStroke(2);
     
       switch (getArrowStyle(i,j)){
           case 0: stroke=new BasicStroke(2);
               break;
           case 1: stroke=new BasicStroke(0);
               break;
           case 2: stroke=new BasicStroke(2.0f,                      // Width
                           BasicStroke.CAP_SQUARE,    // End cap
                           BasicStroke.JOIN_MITER,    // Join style
                           10.0f,                     // Miter limit
                           new float[] {6.0f,12.0f}, // Dash pattern
                           0.0f);                     // Dash phase
               break;
           case 3: stroke=new BasicStroke(2.0f,                      // Width
                           BasicStroke.CAP_SQUARE,    // End cap
                           BasicStroke.JOIN_MITER,    // Join style
                           10.0f,                     // Miter limit
                           new float[] {2.0f,4.0f}, // Dash pattern
                           0.0f);  
       }
      return stroke;
   }
   
   
    public void drawQuiver(Graphics2D g){
        String str;
        Line2D.Double line;
        Rectangle2D rect;
        Point2D.Double P1,Q1;
        int i,j;
        BigInteger un, moinsun;
        
        Boolean drawArrowsBetweenFrozenVertices=Boolean.FALSE;

        updateTrafficLights();

        P1=new Point2D.Double();
        Q1=new Point2D.Double();
        FontMetrics fm = g.getFontMetrics();

        BasicStroke stroke = new BasicStroke(2);
                
                /* new BasicStroke(2.0f,                      // Width
                           BasicStroke.CAP_SQUARE,    // End cap
                           BasicStroke.JOIN_MITER,    // Join style
                           10.0f,                     // Miter limit
                           new float[] {16.0f,20.0f}, // Dash pattern
                           0.0f);                     // Dash phase
        */
                
        g.setStroke(stroke);
        g.setPaint(Color.black);
        
        //drawarrow(new Point2D.Double(50,100), new Point2D.Double(100,50),10,g);
        //drawarrow(new Point2D.Double(50,50), new Point2D.Double(70,50),10,g);
        //drawarrow(new Point2D.Double(50,50), new Point2D.Double(200,70),20,g);
         //drawarrow(new Point2D.Double(40,50), new Point2D.Double(40,150),10,g);

        line = new Line2D.Double();
        g.setPaint(Color.black);
        
        boolean drawThisArrow=true;

        
        
        if (showSpikes){
            g.setPaint(Color.gray);
            Q1.setLocation(P[nbpoints-1].getX(),P[nbpoints-1].getY());
            for (i=0;i<M.nbrows-1;i++){
                P1.setLocation(P[i].getX(),P[i].getY());
                line = new Line2D.Double();
                line.setLine(P1,Q1);
                g.draw(line);
            }
            g.setPaint(Color.black);
        }
        
        if (CM==null){
            for(i=0;i<M.nbrows;i++){
                for(j=i+1;j<M.nbcols;j++){
                        drawThisArrow=drawArrowsBetweenFrozenVertices.booleanValue()||!((P[i].frozen)&P[j].frozen);
                        if ((P[i].frozen||P[j].frozen)&&!showFrozenVertices){drawThisArrow=false;}
                       if (drawThisArrow){
                            if(M.A[i][j].signum() >0){
                              P1.setLocation(P[i].getX(),P[i].getY());
                              Q1.setLocation(P[j].getX(),P[j].getY());
                              g.setStroke(getArrowStroke(i,j));
                              drawarrow(P1,Q1, (double) P[j].r,g);
                              g.setStroke(stroke);
                              //line.setLine(P[i],P[j]);
                              //g.draw(line);
                            }
                            if(M.A[i][j].signum() <0){
                              P1.setLocation(P[j].getX(),P[j].getY());
                              Q1.setLocation(P[i].getX(),P[i].getY());
                              g.setStroke(getArrowStroke(i,j));
                              drawarrow(P1,Q1, (double) P[i].r,g);  
                              g.setStroke(stroke);
                            }
                       }
                   }  
                }
            }
        else {
            for(i=0;i<M.nbrows;i++){
                for(j=0;j<M.nbcols;j++){
                       if (CM.existArrows(i,j)){
                               P1.setLocation(P[i].getX(),P[i].getY());
                                  Q1.setLocation(P[j].getX(),P[j].getY());
                                  g.setPaint(CM.getArrowColor(i,j));
                                  drawarrow(P1,Q1, (double) P[j].r,g);
                       }
                       if (CM.existArrows(j,i)){
                           P1.setLocation(P[j].getX(),P[j].getY());
                              Q1.setLocation(P[i].getX(),P[i].getY());
                              g.setPaint(CM.getArrowColor(j,i));
                              drawarrow(P1,Q1, (double) P[i].r,g);  
                       }
                }
            }
        }
        
        
        for(i=0;i<nbpoints;i++){
            if (P[i].frozen&&!showFrozenVertices){
                continue;
            }
            if (!showLabels){P[i].draw(g);}
            else {P[i].draw(g,""+(i+1));
            }
        }
        
        g.setPaint(Color.black);
        Point2D.Double T=new Point2D.Double();
        un = new BigInteger("1");
        moinsun=new BigInteger("-1");
        BigInteger cent=new BigInteger("100");
        BigInteger v1=null;
        BigInteger v2=null;
        
        Font oldft=g.getFont();
        Font newft=oldft.deriveFont(arrowlabelsize);
        g.setFont(newft);
        fm=g.getFontMetrics();
        
        if (CM!=null){
            for(i=0;i<M.nbrows;i++){
            for(j=0;j<M.nbcols;j++){
                if (CM.existMultArrows(i,j)){
                  str=CM.getMultArrows(i,j);
                  rect = fm.getStringBounds(str, g);
                  double w=rect.getWidth();
                  double h=rect.getHeight();
                  double s=w;
                  if(h>s){s=h;};
                  s=s+P[j].r;
                  s=0.7*s/P[i].distance(P[j]);
                  double tx =  (P[j].x + 0.5*(P[i].x-P[j].x));
                  double ty =  (P[j].y + 0.5*(P[i].y-P[j].y));
                  tx=tx-w/2;
                  ty=ty-h/2;
                  g.setPaint(Color.white);
                  rect.setRect(tx,ty,w,h);
                  g.fill(rect);
                  g.setPaint(Color.black);
                  g.drawString(str, (float)tx, (float) (ty+0.75*h));
                }
            }}
        }
        
        if (CM==null){
        for(i=0;i<M.nbrows;i++){
        for(j=i+1;j<M.nbcols;j++){
        if (drawArrowsBetweenFrozenVertices.booleanValue()||!((P[i].frozen)&(P[j].frozen))){
            boolean needarrow=false;
            v1=M.A[i][j].abs();
            v2=M.A[j][i].abs();
            needarrow=((v1.compareTo(un)>0)||(v2.compareTo(un)>0));
            if (needarrow){
                 if (v1.compareTo(v2)==0){
                     if ((!shortNumbers)||(v1.compareTo(cent)<0)){ 
                        str=""+v1;}
                     else {
                         str="inf";
                     }
                  }
                  else{
		      if (M.A[i][j].signum()>0){
                      str=""+v1+","+v2;
			  }
			  else {
			      str=""+v2+","+v1;
			  }
                  }
                
                  rect = fm.getStringBounds(str, g);
                  double w=rect.getWidth();
                  double h=rect.getHeight();
                  double s=w;
                  if(h>s){s=h;};
                  s=s+P[j].r;
                  s=0.7*s/P[i].distance(P[j]);
                  double tx =  (P[j].x + 0.5*(P[i].x-P[j].x));
                  double ty =  (P[j].y + 0.5*(P[i].y-P[j].y));
                  tx=tx-w/2;
                  ty=ty-h/2;
                  g.setPaint(Color.white);
                  rect.setRect(tx,ty,w,h);
                  g.fill(rect);
                  g.setPaint(Color.black);
                  g.drawString(str, (float)tx, (float) (ty+0.75*h));
            }
        }}}}
    }

public Rectangle strictEnclosingRectangle(){
    int i;
        double xmin,xmax,ymin,ymax,x,y;
        
        if (nbpoints==0){
            xmin=0;
            xmax=10;
            ymin=0;
            ymax=10;
        }
        else {
            xmin=P[0].getX();
            xmax=xmin;
            ymin=P[0].getY();
            ymax=ymin;
            for (i=1;i<nbpoints; i++){
                x=P[i].getX();
                y=P[i].getY();
                if (x<=xmin){xmin=x;}
                if (x>xmax){xmax=x;}
                if (y<ymin){ymin=y;}
                if (y>ymax){ymax=y;}
            }
        }
        
        //System.out.println("xmin:"+xmin+ " xmax:"+xmax + " ymin"+ymin +" ymax:"+ymax);
        Rectangle r= new Rectangle( (int) xmin, (int) ymin, (int) (xmax-xmin), (int) (ymax-ymin));
        return r;
}
    
    public Rectangle enclosingRectangle(){
        Rectangle r= strictEnclosingRectangle();
        r.grow(25,15);
        return r;
    }
    
    public void fitToGrid(int gridsize){
        int i,x,y;
        
        for (i=0; i<nbpoints; i++){
            x=(int) Math.round(P[i].getX()/gridsize)*gridsize;
            y=(int) Math.round(P[i].getY()/gridsize)*gridsize;
            P[i].setLocation(x,y);
        }
    }

    public void scalecenter(Rectangle r2){
        double h1, h2, w1, w2, cw, ch, c, tx, ty;
        Point2D.Double Pt;
        Rectangle r1;
        int i;
        
       
        //System.out.print(this);
        r1=enclosingRectangle();
        r1.grow((int)(r1.getWidth()*growthFactor),(int)(r1.getHeight()*growthFactor));
        //System.out.println("Scale and Center of "+r1);
        
        h1=r1.getHeight();
        h2=r2.getHeight();
        w1=r1.getWidth();
        w2=r2.getWidth();
        
        if ((h2>10)&(w2>10)){
            ch=h2/h1;
            cw=w2/w1;

            if (ch<=cw){c=ch;} else{c=cw;}
            tx=r2.getX()+r2.getWidth()/2-c*r1.getCenterX();
            ty=r2.getY()+r2.getHeight()/2-c*r1.getCenterY();

            AffineTransform T=new AffineTransform(c,0,0,c,tx,ty);

            Pt=new Point2D.Double();
            for (i=0; i<nbpoints; i++){
                Pt.setLocation(P[i]);
                T.transform(Pt,Pt);
                P[i].setLocation((int)Pt.getX(), (int) Pt.getY());
            }
        }
    }

    public void stretchcenter(Rectangle r2){
        double h1, h2, w1, w2, cw, ch, c, tx, ty;
        Point2D.Double Pt;
        Rectangle r1;
        int i;

        //System.out.print(this);
        r1=enclosingRectangle();
        r1.grow((int)(r1.getWidth()*growthFactor),(int)(r1.getHeight()*growthFactor));
        //System.out.println("Scale and Center of "+r1);

        h1=r1.getHeight();
        h2=r2.getHeight();
        w1=r1.getWidth();
        w2=r2.getWidth();

        if ((h2>10)&(w2>10)){
            cw=h2/h1;
            ch=w2/w1;

            //if (ch<=cw){c=ch;} else{c=cw;}
            tx=r2.getX()+r2.getWidth()/2-ch*r1.getCenterX();
            ty=r2.getY()+r2.getHeight()/2-cw*r1.getCenterY();

            AffineTransform T=new AffineTransform(ch,0,0,cw,tx,ty);

            Pt=new Point2D.Double();
            for (i=0; i<nbpoints; i++){
                Pt.setLocation(P[i]);
                T.transform(Pt,Pt);
                P[i].setLocation((int)Pt.getX(), (int) Pt.getY());
            }
        }
    }
    
    public void horscalecenter(Rectangle r2){
        double h1, h2, w1, w2, cw, ch, c, tx, ty;
        Point2D.Double Pt;
        Rectangle r1;
        int i;
        
        //System.out.print(this);
        r1=enclosingRectangle();
        r1.grow((int)(r1.getWidth()*growthFactor),(int)(r1.getHeight()*growthFactor));
        //System.out.println("Scale and Center of "+r1);
        
        h1=r1.getHeight();
        h2=r2.getHeight();
        w1=r1.getWidth();
        w2=r2.getWidth();
        
        if ((h2>10)&(w2>10)){
            ch=h2/h1;
            cw=w2/w1;

            if (ch<=cw){c=ch;} else{c=cw;}
            tx=r2.getX()+r2.getWidth()/2-c*r1.getCenterX();
            ty=r2.getY()+r2.getHeight()/2-r1.getCenterY();

            AffineTransform T=new AffineTransform(c,0,0,1,tx,ty);

            Pt=new Point2D.Double();
            for (i=0; i<nbpoints; i++){
                Pt.setLocation(P[i]);
                T.transform(Pt,Pt);
                P[i].setLocation((int)Pt.getX(), (int) Pt.getY());
            }
        }
    }
    }


class Seed  {
    Quiver Q, Qmem;
    Cluster C;
    TextDisplayPane dPane;
    QuiverDrawing qd;
    BMatrix CM;
    BMatrix CMIT;
    BMatrix Tau;
    
    public Quiver getMemQuiver(){
        return Qmem;
    }

    public void setMemQuiver(Quiver argQ){
        Qmem=argQ;
    }

    public void initializeCartanMatrix(){
        CMIT=new BMatrix(Q.M.nbcols, Q.M.nbrows);
        CMIT.copyPosPartfrom(Q.M);
        CMIT.transpose();
        CMIT.negate();
        CMIT.addIdentity();
        CM=new BMatrix(CMIT);
        CM.transpose();
        try {
        CM.invert();
        Tau=new BMatrix(CMIT);
        Tau.multiplyby(CM);
        Tau.transpose();
        Tau.negate();
        System.out.println("Cartan Matrix: \n"+CM+ "\n Inverse Transpose: \n"+CMIT+ "\n Tau: \n"+Tau);
        }
        catch (Exception e) {
            System.out.println("Error: "+ e.getMessage());
            CM=null;
            CMIT=null;
            Tau=null;
        }
    }
    
    public BigInteger weight(){
        return Q.weight();
    }
    
    public static int[] invPerm(int[] p){
        int n,i,j;
        int[] q;
        
        n=p.length;
        q=new int[n];
        for (i=0; i<n; i++){
            j=0;
            while (p[j]!=i){j++;}
            q[i]=j;
        }
        return q;
    }
    

    public void write (BufferedWriter out){
	Q.write(out);
	try{
            out.write("//Cluster is null"); out.newLine();
            /*
            if (C==null){
                out.write("//Cluster is null"); out.newLine();
            }
             */
        
        /*
	else{
            String s="//Cluster: ";
            if (C.variablesActive()){s=s+"Variables ";}
            if (C.dvectorsActive()){s=s+"Dimensions";}
	    out.write(s);out.newLine();
	    C.write(out);
	}
         */
	}
	catch (IOException e){
	    System.out.println("I/O Exception in writing Seed: " + e.getMessage());
	}
    }

    public void read (BufferedReader in){
	Q.read(in);
        //System.out.println(qd.lastReadLine);
	try{
	String str;
        if (qd.lastReadLine!=null){str=qd.lastReadLine;} else {str=in.readLine();}
	//System.out.println("First line read in Seed:" + str);
	if (str.equals("//Cluster is null")){
	    C=null;
            dPane=null;
            qd.lastReadLine=null;
	   }
	else{
	    C=null;
            boolean v=(str.indexOf("Variables")>0);
            boolean d=(str.indexOf("Dimensions")>0);
            if (!(v||d)){v=true; d=true;}
	    C=new Cluster(Q.nbpoints);
	    C.read(in, str);
	    if (dPane!=null){
            dPane.setText(C.toString());
	    }
	    else{
                activateDPane();
		//dPane=new TextDisplayPane(C.toString());
		//Dimension preferredSize = new Dimension(100, 20);
		//dPane.setPreferredSize(preferredSize);
	   }
	   }
	}
	catch (IOException e){
	    System.out.println("I/O Exception in reading Seed: " + e.getMessage());
	}
        /* if (qd.lastReadLine!=null){
            //System.out.println("Last read line: "+qd.lastReadLine);
            //System.out.println("Reading history.");
            Q.Hist.read(in);
        } */
         
    }

    public Seed(int choice, int param, QuiverDrawing qdarg){
        
        qd=qdarg;
        Q=new Quiver(choice, param, qd);
        Qmem=null;
        C=null;
        dPane=null;
        
        //C=new Cluster(param*(param+1)/2, true,true);
        
    }
    
    public void reset(int i, QuiverDrawing qd){
        Q=null;
        Q=new Quiver(Quiver.GLSAN, i, qd);
        if (C!=null){
            C.removeAllMutables();
            C=null;
        }
        
    }
    
    public void deletepoint(int i){
        Q.deletenode(i);
        if (C!=null){
            C=null;
            C=new Cluster(Q.nbpoints);
            dPane.setText(C.toString());
        }
    }
    
    public void addpoint(int x, int y){
        Q.addnode(x,y);
        if (C!=null){
            //C.addvar();
            dPane.append(C.toString(Q.nbpoints-1));
        }
        
    }

    public void addwpoint(int x, int y){
        Q.addwnode(x,y);
        if (C!=null){
            //C.addvar();
            dPane.append(C.toString(Q.nbpoints-1));
        }

    }
    
    public void adddoublewpoint(int x, int y, boolean altdown){
        Q.addDoubleWNode(x,y, altdown);
        if (C!=null){
            //C.addvar();
            dPane.append(C.toString(Q.nbpoints-1));
        }

    }
 
    public Seed(int choice, int param, QuiverDrawing argqd, boolean hasCluster, JSplitPane splitPane){
   
        qd=argqd;
        Q=new Quiver(choice, param, qd);
        Qmem=null;
        if (hasCluster){
            C=new Cluster(param*(param+1)/2);
            activateDPane();
        }
    }
    
    public void permuteNodes(int[] perm){
        Q.permuteVertices(perm);
        if (C!=null){C.permuteVertices(perm); dPane.append(C.toString());}
    }
    
    public void art(int i){
        System.out.println("Auslander-Reiten translation of node "+i);
        int cter=0;
        int limit=10*Q.nbpoints;
        int m=Q.firstSource(i);
        while ((cter<limit) && (m!=-1)){
            mutate(m);
            m=Q.firstSource(i);
            cter++;
        }
        if (m==-1){
            mutate(i);
        }
        else {
            JOptionPane.showMessageDialog(qd,"Auslander-Reiten translation failed. \nCould not transform "+i+
                    " into a sink using "+cter+ " mutations");
        }
    }
    
    public void artminus(int i){
        System.out.println("Negative Auslander-Reiten translation of node "+i);
        int cter=0;
        int limit=10*Q.nbpoints;
        int m=Q.firstTarget(i);
        while ((cter<limit) && (m!=-1)){
            mutate(m);
            m=Q.firstTarget(i);
            cter++;
        }
        if (m==-1){
            mutate(i);
        }
        else {
            JOptionPane.showMessageDialog(qd,"Negative Auslander-Reiten translation failed. \nCould not transform "+i+
                    " into a sink using "+cter+ " mutations");
        }
    }
    
    
    
    public void addMutable(Mutable toAdd){
        if (C==null){
            C=new Cluster(Q.nbpoints);
        }
        C.addMutable(toAdd);
        activateDPane();
    }
    
    
    
    
    public void removeMutable(String Name){
        if (C!=null){
            C.removeMutable(Name);
            if (!C.hasData()){
                C=null;
            }
        }
    }
    
    public Mutable getMutable(String Name){
        if (C!=null){
            return C.getMutable(Name);
        }
        return null;
    }
    
    public void removeAllMutables(){
        if (C!=null){
            C.removeAllMutables();
            if (!C.hasData()){
                C=null;
            }
        }
    }
   
    public void swapQmem(){
        if (Qmem==null){
            Qmem=new Quiver(Quiver.GLSAN,4,qd);
        }
        if (Q.SeqDia!=null){Q.SeqDia.setVisible(false);}
        Quiver temp=Qmem;
        Qmem=Q;
        Q=temp;
    }
    
    public void product(SequencesDialog sd){
        Quiver Q1=Q;
        int n1=Q1.nbpoints;
        int[] I1=Q1.sources();
        int[] J1=Q1.sinks();
        Quiver Q2=Qmem;
        int n2=Q2.nbpoints;
        int[] I2=Q2.sources();
        int[] J2=Q2.sinks();
        
        if ((I1!=null)&(I2!=null)&(J1!=null)&(J2!=null)){
            int[] I1I2=Utils.product(I1,I2,n2);
            int[] I1J2=Utils.product(I1,J2,n2);
            int[] J1I2=Utils.product(J1,I2,n2);
            int[] J1J2=Utils.product(J1,J2,n2);

            String[] seq=new String[7];
            seq[0]=Utils.toString(I1J2);
            seq[1]=Utils.toString(J1I2);
            seq[2]=Utils.toString(I1I2);
            seq[3]=Utils.toString(J1J2);
            seq[4]=Utils.toString(I1I2)+Utils.toString(J1J2);
            seq[5]=Utils.toString(I1J2)+Utils.toString(J1I2);
            seq[6]=seq[4]+seq[5];
            sd.setSequences(seq);
        
            seq[0]="IJ";
            seq[1]="JI";
            seq[2]="II";
            seq[3]="JJ";
            seq[4]="II * JJ=t+";
            seq[5]="IJ * JI=t-";
            seq[6]="t+ t-";
            sd.setNames(seq);

            //System.out.println("I1: "+ Utils.toString(I1));
            //System.out.println("J1: "+ Utils.toString(J1));
            //System.out.println("I2: "+Utils.toString(I2));
            //System.out.println("J2: "+Utils.toString(J2));
            //System.out.println("I1J2: "+seq[0]);
            //System.out.println("J1I2: "+seq[1]);
        }
        
        BigInteger[][] A=Q1.M.A;
        BigInteger[][] B=Q2.M.A;
        BigInteger[][] C=new BigInteger[n1*n2][n1*n2];
        for (int i=0; i<n1*n2; i++){
            for (int j=0; j<n1*n2; j++){
                C[i][j]=BigInteger.ZERO;
            }
        }
        for (int j2=0; j2<n2; j2++){
            for (int i1=0; i1<n1; i1++){
                for (int j1=0; j1<n1; j1++){
                    C[i1*n2+j2][j1*n2+j2]=new BigInteger(A[i1][j1].toString());
                    C[j1*n2+j2][i1*n2+j2]=new BigInteger(A[j1][i1].toString());
                }
            }
        }
        for (int i1=0; i1<n1; i1++){
            for (int i2=0; i2<n2; i2++){
                for (int j2=0; j2<n2; j2++){
                    C[i1*n2+i2][i1*n2+j2]=new BigInteger(B[i2][j2].toString());
                    C[i1*n2+j2][i1*n2+i2]=new BigInteger(B[j2][i2].toString());
                }
            }
        }
        
        for (int i1=0; i1<n1; i1++){
            for (int j1=0; j1<n1; j1++){
                for (int i2=0; i2<n2; i2++){
                    for (int j2=0; j2<n2; j2++){
                        int s1=A[i1][j1].compareTo(BigInteger.ZERO);
                        int s2=B[i2][j2].compareTo(BigInteger.ZERO);
                        if (s1==s2){
                               C[j1*n2+j2][i1*n2+i2]=(A[i1][j1].multiply(B[i2][j2])).multiply(new BigInteger(""+s1));    
                               C[i1*n2+i2][j1*n2+j2]=(A[j1][i1].multiply(B[j2][i2])).multiply(new BigInteger(""+(-s1)));    
                            }
                        }
                    }
                }
        }
        
        Q=new Quiver(new BMatrix(C),qd);
        Q.setSeqDia(sd);
        
        int x=0;
        int y=0;
        for (int i1=0; i1<n1; i1++){
            for (int i2=0; i2<n2; i2++){
                x=(int) Math.round(i1*10);
                y=-(int) Math.round(i2*10);
                Q.P[i1*n2+i2].setLocation(x,y);
            }
        }
        Rectangle r=qd.getBounds();
        if (r.getWidth()==0){r.setRect(0,0,350,300);}
        Q.scalecenter(r);
    }
    
    public void updateDPane(){
        if (dPane!=null){
            dPane.append(C.toString());
        }
    }
    
    public void activateDPane(){
        //JOptionPane.showMessageDialog(this,"About to display clusters." );
        if (dPane==null){
            dPane=new TextDisplayPane("");
            Dimension preferredSize = new Dimension(100, 20);
            dPane.setPreferredSize(preferredSize);
            dPane.setFontSize(20);
        }
    }
    
    
    public void deactivateDPane(){
            if (dPane!=null){
                dPane.setText("");
            }
            dPane=null;
    }
    
    public void resetCluster(){
    if (C!=null){
       C.reset();
       dPane.setText(C.toString());
    }
    }

    public String toString(){
	return Q.toString();
    }
    
    public void mutate(int k){
        mutate(k,1);
    }

    public void mutate(int[] seq){
        if (seq!=null){
           for (int i=0; i<seq.length; i++){
               mutate(seq[i]);
           }
           qd.repaint();
        }
    }

    public void mutate(int[] seq, int[] perm){
        if (seq!=null){
           for (int i=0; i<seq.length; i++){
               mutate(seq[i]);
           }
           if (perm!=null){
                permuteNodes(perm);
           }
        }
      qd.repaint();
    }

    public void invmutate(int[] seq, int[] perm){
        if (perm!=null){
           int[] invp=invPerm(perm);
           permuteNodes(invp);
        }
        if (seq!=null){
            for (int i=seq.length-1; i>=0; i--){
                mutate(seq[i]);
            }
        }
        qd.repaint();
    }

    public int mutorder(String s1, String s2, int bound){
        return mutorder(Utils.StringToIntArray(s1), Utils.StringToIntArray(s2),bound);
    }

    public int mutorder(int[] seq, int[] perm, int bound){
       BMatrix InitialMatrix=new BMatrix(Q.M);
       MoveablePoint[] InitialP = new MoveablePoint[Q.nbpoints];

       for (int i=0;i<Q.nbpoints;i++){
           InitialP[i]=Q.P[i];
        }

       boolean found=false;
       int z=1;
       while ((z<bound) & (!found)) {
           mutate(seq,perm);
           found=(InitialMatrix.equals(Q.M));
           z=z+1;
       }

       Q.M=InitialMatrix;
       Q.P=InitialP;

       if (!found){
           Q.tauorder=-1;
           return -1;
       }
       else {
           Q.tauorder=z-1;
           return z-1;
       }
    }

    public void marknodes(int[] seq){
        if (seq!=null){
           for (int i=0; i<seq.length; i++){
                  Q.P[seq[i]].toggleMarking();
           }
        }
        qd.repaint();
    }




    
    public void mutate(int k, int dir){
        
        if (C!=null){
          C.mutate(Q,k,dir);
          dPane.append(C.toString(k));
        }
        
        Q.mutate(k,dir);
       
        //System.out.println(C);
    }
}

class SearchDialog extends JDialog implements ActionListener{
    Random MyRandom;
    Quiver Q;
    boolean stopButtonPressed;
    JLabel presentWeightLabel;         
    JLabel minWeightLabel;
    
    
    void makeLighter(){
        BMatrix Mrun=new BMatrix(Q.nbpoints,Q.nbpoints);
        Mrun.copyfrom(Q.M);
        BMatrix Mmin=new BMatrix(Q.nbpoints,Q.nbpoints);
        Mmin.copyfrom(Q.M);
        BigInteger minweight=Mmin.weight();
        BigInteger runweight=Mrun.weight();
        BigInteger overflow=new BigInteger("1000000000");
        int mini=0;
        int i=0;
        presentWeightLabel.setText("Present weight: "+runweight.toString());
        minWeightLabel.setText    ("Minimal weight: "+minweight.toString());
        while (!stopButtonPressed & (runweight.compareTo(overflow)<0)){
            
            int r=MyRandom.nextInt(Q.nbpoints);
            Mrun.mutate(r);
            runweight=null;
            runweight=Mrun.weight();
            presentWeightLabel.setText("Present weight: "+runweight.toString());
            minWeightLabel.setText    ("Minimal weight: "+minweight.toString());
            if (runweight.compareTo(minweight)<=0){
                mini=i;
                Mmin.copyfrom(Mrun);
                minweight=null;
                minweight=new BigInteger(runweight.toString());
                //System.out.println("Iteration: "+i+", Weight: "+ minweight);
            }
            i++;
            //System.out.println("Iteration: " +i + ", Weight: "+ runweight);
        }
        if (runweight.compareTo(overflow)>=0){
            JOptionPane.showMessageDialog(this,"Computation aborted after "+ i + " iterations.\n"+
                    "Last weight attained: "+runweight);
        }
        JOptionPane.showMessageDialog(this,"Minimal weight found: "+minweight+
                "\n"+"Iterations: "+mini);
        Q.M.copyfrom(Mmin);
}
    
    public void actionPerformed(ActionEvent e) {
        if ("Stop".equals(e.getActionCommand())) {
        setVisible(false);
        stopButtonPressed=true;
        }
    }
    
    
    public SearchDialog(Frame frame, Component locComp, String title, Quiver Qarg, int height, int width) {
        super(frame, title, false);
        Q=Qarg;
        
        stopButtonPressed=false;
        MyRandom = new Random();
        
        JButton StopButton = new JButton("Stop");
        StopButton.addActionListener(this);
        
        getRootPane().setDefaultButton(StopButton);
        
        
        Box statusPanel = new Box(BoxLayout.Y_AXIS);
        presentWeightLabel=new JLabel("Present weight:             ");
        minWeightLabel=new JLabel    ("Minimal weight:             ");
        
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(presentWeightLabel);
        statusPanel.add(minWeightLabel);
        
        //getContentPane().add(statusPanel, BorderLayout.SOUTH);
        
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        //buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(StopButton);
        

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(statusPanel, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locComp);
        setVisible(true);
        makeLighter();
    }
        
}

class TextDisplayDialog extends JDialog implements ActionListener {
    protected JTextArea textArea;
    private static TextDisplayDialog dialog;
    
    
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
        }
        TextDisplayDialog.dialog.setVisible(false);
    }
    
    public static void showDialog(Component frameComp,
                                    Component locationComp,
                                    String title,String text,int height, int width) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new TextDisplayDialog(frame,
                                locationComp,
                                title,text,height,width);
        dialog.setVisible(true);
    }
    
    
    public TextDisplayDialog(Frame frame, Component locComp, String title, String str, int height, int width) {
        super(frame, title, false);
        JButton OKButton = new JButton("OK");
        OKButton.addActionListener(this);
        
        getRootPane().setDefaultButton(OKButton);
        
        textArea = new JTextArea(height, width);
        textArea.setText(str);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel textPane = new JPanel();
        textPane.setLayout(new BoxLayout(textPane, BoxLayout.PAGE_AXIS));
        
        
        textPane.add(Box.createRigidArea(new Dimension(0,5)));
        textPane.add(scrollPane);
        textPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(OKButton);
        

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(textPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locComp);
    }
    
}

class TextDisplayPane extends JPanel{
    JTextArea textArea;
    
    public void print(Graphics g){
        textArea.print(g);
    }
    
     public void setText(String str){
      textArea.setText(str);
      textArea.setCaretPosition(textArea.getText().length());
    }
     
    public String getText(){
        return textArea.getText();
    }
    
    public void append(String str){
      textArea.append(str);
      textArea.setCaretPosition(textArea.getText().length());
    }
    
    public int getFontSize(){
        Font font=textArea.getFont();
        return font.getSize();
    }
    
    public void setFontSize(int size){
        Font font=textArea.getFont();
        Font newFont= new Font(font.getName(), font.getStyle(), size);
        textArea.setFont(newFont);
    }
    public TextDisplayPane(String str) {
        
        textArea = new JTextArea(5,41);
        //textArea.setPreferredSize(new Dimension(500,500));
        textArea.setText(str);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea,
                                       JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
}

class History  {
    Vector history;
    int historycounter;
    QuiverDrawing qd;
    public static final int END_OF_HISTORY=-1;

    public String toString(){
        String s="";
        if (history.size()>0){
            s=s+(1+((Integer) history.get(0)).intValue());
        }
        for (int i=1; i<history.size(); i++){
            s=s+","+(1+((Integer) history.get(i)).intValue());
        }
	return s;
    }
    
    public int getlength(){
        return history.size();
    }
    public void permute(int[] perm){
       Vector newhistory=new Vector(10,1); 
       int vali;
       for (int i=0; i<history.size(); i++){
           vali=((Integer) history.get(i)).intValue();
           newhistory.add(new Integer(perm[vali]));
	    }
       history=newhistory;
    }

    public void read (BufferedReader in){
	String str;
	String patternstr=" ";
	String[] fields;

	try{
	    str=in.readLine();
            if ("//Historycounter".equals(str)){
                qd.setLastReadLine(null);
                str=in.readLine();
                historycounter=Integer.parseInt(str);
                in.readLine();
                str=in.readLine();
                fields=str.split(patternstr);
                history=null;
                history=new Vector();
                //System.out.println("History's last read string:" + str);
                if (!str.equals("")){
                    for (int i=0; i<fields.length; i++){
                        history.add(new Integer(Integer.parseInt(fields[i])));
                    }
                }
            }
            else {
                qd.setLastReadLine(str);
            }
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
	    

    public void write(BufferedWriter out){
	try{
	    out.write("//Historycounter"); out.newLine();
	    out.write(""+historycounter); out.newLine();
	    out.write("//History"); out.newLine();
	    //System.out.println("History size:" + history.size());
	    for (int i=0; i<history.size(); i++){
		out.write(history.get(i)+" ");
	    }
	    out.newLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }


    public History(QuiverDrawing argqd){
	history=new Vector(10,1);
        qd=argqd;
	historycounter=-1;
    }

    public void reset(){	
        history.removeAllElements();
	historycounter=-1;
	updatebuttons();
    }

    public void updatebuttons(){
        //System.out.println("Updatebuttons: cter: "+historycounter + " size: "+history.size());
        qd.updatehistorybuttons(historycounter, history.size());
    }

 
 public void show(Frame f){
        TextDisplayDialog.showDialog(f,f,"History", toString(),5,40);
 }
 
 
    public void add(int i){
        if (historycounter<history.size()-1){
	     history.setSize(historycounter+1);
	 }
	 history.add(new Integer(i));
	 historycounter++;
         updatebuttons();
    }
   
    public int present(){
        if (historycounter<0){
	    return END_OF_HISTORY;}
	else {
	    return ((Integer) history.elementAt(historycounter)).intValue();
	}
    }
    
    
    public int next(){
        if (historycounter>history.size()-2){
	    return END_OF_HISTORY;}
	else {
	    int mutvertex=((Integer) history.elementAt(historycounter+1)).intValue();
	    return mutvertex;
	}
    }
    
    public int back(){
	if (historycounter<0){
	    return END_OF_HISTORY;}
	else {
	    int hc=historycounter;
            historycounter=historycounter-1;
	    updatebuttons();
	    return ((Integer) history.elementAt(hc)).intValue();
	}
    }
    
    public int shiftedEntry(int shift){
        int k=historycounter+shift;
        if ((k<0)||(k>=history.size())){return -1;}
        return ((Integer) history.elementAt(k)).intValue();
    }
    

    public int forward(){
	if (historycounter>history.size()-2){
	    return END_OF_HISTORY;}
	else {
            historycounter++;
	    updatebuttons();
	    int mutvertex=((Integer) history.elementAt(historycounter)).intValue();
	    return mutvertex;
	}
    }
 

}

class Utils {
    public final static String au="au";
    public final static String wav="wav";
    public final static String  mid="mid";
    public final static String aif="aif";
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String qmu= "qmu";
    
    
    public static double length(double[] a){
        double l=0;
        for (int i=0; i<a.length; i++){
            l=l+a[i]*a[i];
        }
        return Math.sqrt(l);
    }
    
    public static void markbranch(BMatrix M, int i, Double[] D){
        double x;
        System.out.println("Entering markbranch: i="+i+" D:\n"+Utils.toString(D)+"\nM:"+M);
        for (int j=0; j<M.nbrows; j++){
            if ((D[j]==null)&&(M.A[i][j].compareTo(BigInteger.ZERO)!=0)){
                x=M.A[i][j].negate().doubleValue()/M.A[j][i].doubleValue();
                D[j]=new Double(D[i].doubleValue()*x);
                markbranch(M, j, D);
            }
        }
    }
    
    public static BMatrix antisymmetrizingDiag(BMatrix M){
        int n=M.nbrows;
        Double[] D=new Double[n];
        for (int i=0; i<n; i++){
            D[i]=null;
        }
        boolean nullfound=false;
        int i=0;
        int firstnull=0;
        while (firstnull<n){
            D[firstnull]=new Double(1);
            markbranch(M, firstnull, D);
            firstnull=0; 
            i=0;
            nullfound=false;
            while ((i<n)&&(!nullfound)){
                firstnull++; 
                i++;
                if (i>=n){
                    nullfound=false;
                }
                else {
                    nullfound=(D[i]==null);
                }
            }
        }
        System.out.println("D: "+Utils.toString(D));
        BigInteger[] Dint=makeBigInteger(D);
        BMatrix Dia=new BMatrix(n,n);
        Dia.makeZero();
        for (i=0;i<n;i++){Dia.A[i][i]=Dint[i];}
        System.out.println("M:\n"+M);
        System.out.println("Dia:\n"+Dia);
        BMatrix P=new BMatrix(Dia);
        P.multiplyby(M);
        System.out.println("P:\n"+P);
        if (P.isAntisymmetric()){
            return Dia;
        }
        else {
            return null;
        }
    }
    
    public static BigInteger[] makeBigInteger (Double[] D){
        BigInteger fact=BigInteger.ONE;
        BigInteger b1, b2;
        double fp;
        for (int i=0; i<D.length; i++){
            fp=D[i].doubleValue()-Math.floor(D[i].doubleValue());
            if (fp>0.001){
                b1=new BigInteger(""+Math.round(1/fp));
                b2=b1.gcd(fact);
                fact=fact.multiply(b1).divide(b2);
            }
        }
        BigInteger[] Dint=new BigInteger[D.length];
        for (int i=0; i<D.length;i++){
            Dint[i]=new BigInteger(""+Math.round(fact.longValue()*D[i].doubleValue()));
        }
        return Dint;
    }
    
    public static double angle(double[] a, double[] b, double[] c){
        double[] u=new double[a.length];
        double[] v=new double[a.length];
        double s=0;
        for (int i=0; i<a.length; i++){
            u[i]=b[i]-a[i];
            v[i]=c[i]-a[i];
            s=s+u[i]*v[i];
        }
        s=s/length(u);
        s=s/length(v);
        return s;
    }
    
    public static double dist(double[] a, double[] b){
        double d=0;
        for (int i=0;i<a.length;i++){
            d=d+(a[i]-b[i])*(a[i]-b[i]);
        }
        return Math.sqrt(d);
    }
    
    public static String doubleMatToString(double[][] A){
        String s="";
        for (int i=0; i<A.length; i++){
            for (int j=0; j<A.length; j++){
                s=s+" "+Double.toString(A[i][j]);
            }
            s=s+"\n";
        }
        return s;
    }
    
    public static double scal(double[][] A, int l, double[][] B, int k, double[][] C){
        // computes the product of the transpose of the lth column of A with C with the
        // kth column of B
        double res=0;
        int n=A.length;
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                res=res+A[i][l]*C[i][j]*B[j][k];
            }
        }
        return res;
    }
    
    public static double[][] GramSchmidt(BMatrix M){
        // returns an orthogonal basis for the symmetric invertible Matrix M
        
        int i,j,k,l,n;
        double c;
        double[][] C=M.toDouble();
        System.out.println("C:\n"+doubleMatToString(C));
        Jama.Matrix CMa=new Jama.Matrix(C);
        System.out.println(CMa);
        n=C.length;
        double[][] A=new double[n][n];
        for (i=0; i<n; i++){
            for (j=0;j<n;j++){
                A[i][j]=0;
                if (i==j){A[i][j]=1;}
            }
        }
        System.out.println("A:\n"+doubleMatToString(A));
        for (k=0;k<n;k++){
            for (l=0;l<k;l++){
                c=scal(A,l,A,k,C);
                for (i=0;i<n;i++){
                    A[i][k]=A[i][k]-A[i][l]*c;
                }
                System.out.println("A: c="+c+" k="+k+ " l="+l+"\n"+doubleMatToString(A));
            }
            c=scal(A,k,A,k,C);
            c=Math.sqrt(Math.abs(c));
            if (c>0.001){
                c=1/c;
                for (i=0;i<n;i++){
                    A[i][k]=c*A[i][k];
                }
                System.out.println("A after normalizing column "+k+":\n"+doubleMatToString(A));
            }
        }
        return A;
    }
    
    
    public static int factorial(int n){
        System.out.println("Call of Factorial with n="+n);
        if (n==1){return 1;}
        return n*factorial(n-1);
    }
    
    public static void traverseTree(int n, int v, int last, String s){
        System.out.println("Call of traverseTree with n="+n+ " last="+last+" s="+s);
        if (n==0){return;}
        for (int i=0; i<v; i++){
        if (i!=last){
            System.out.println("Mutate forward at "+i);
            traverseTree(n-1,v, i, s+i);
            System.out.println("Mutate back    at "+i);
            }
        }
    }
    
    public static int[] ExponentVector(RingElt r, Ring R, int n){
                //System.out.println(r);
        
        int[] v=new int[n];
        for (int i=0; i<n; i++){
            v[i]=0;
        }
        
        if (r.equals(R.one())){
            return v;
        }

        PolynomialRing R1=(PolynomialRing) ((QuotientField) R).getBaseRing();

        for (int j=0;j<n;j++){
            //System.out.println(R1);
            //System.out.println("i,j,d:" +i+" "+j+" " +d);
            int d=R1.degree(r);
            v[n-1-j]=d;
            r=R1.getCoefficientAt(d,r);
            if (j+1<n){R1=(PolynomialRing) R1.getCoefficientRing();}
        }
        return v;
    }
    
     public static RingElt yexp(Ring RF, BigInteger[] e){
        RingElt r=RF.one();
        int size=e.length;
        for (int i=0; i<size; i++){
            r=RF.mult(r, RF.pow(RF.map("y"+(i+1)),e[i]));
        }
        return r;
    }
    
    public static BigInteger[] scalmult(BigInteger a, BigInteger[] b){
        int n=b.length;
        BigInteger[] c=new BigInteger[n];
        for (int i=0; i<n; i++){
            c[i]=b[i].multiply(a);
        }
        return c;
    }
    
    public static BigInteger min(BigInteger a, BigInteger b){
        if (a.compareTo(b)>0){
            return b;
        }
        else {
            return a;
        }
    }
    
    public static int[] product (int[] u, int[] v, int n2){
        int[] uv=new int[u.length*v.length];
        for (int i=0; i<u.length; i++){
            for (int j=0; j<v.length; j++){
                uv[i*v.length+j]=u[i]*n2+v[j]+1;
            }
        }
        return uv;
    }
    
    public static String replaceAll(String str, String repl, String replby){
        StringBuffer buffer=new StringBuffer(str);
        replaceAll(buffer, repl, replby);
        return new String(buffer);
    }
    
    public static void replaceAll(StringBuffer str, String repl, String replby){
        
        //System.out.println("Replace "+repl+" by " + replby + " in " + str);
        //System.out.println("Before: " + str);
        int from=0;
        int start=str.indexOf(repl,from);
        //System.out.println("Search "+repl+" in "+res+ ": "+start);
        while (start>=0){
              int end=start+repl.length();
              str.replace(start,end,replby);
              from=end;
              start=str.indexOf(repl,from);
              }
        //System.out.println("After: "+str);
    }
    
    public static boolean omnipresent(BigInteger[] v){
        boolean ans=true;
        for (int i=0; i<v.length; i++){
            if (v[i].compareTo(BigInteger.ZERO)==0){ans=false;}
        }
        return ans;
    }
    
    public static int[] StringToIntArray(String str){
        if ((str==null) || (str.length()==0)){
            return null;
        }
        String[] fields=str.split(" ");
        int[] seq=new int[fields.length];
        for (int i=0; i<fields.length; i++){
            seq[i]=Integer.parseInt(fields[i])-1;
        }
        return seq;
    }
    
    
    public static String toString(Double[] v){
        if (v==null){
            return "null";
        }
        String s="";
        for (int i=0; i<v.length;i++){
            if (v[i]==null){
                s=s+"null\n";
            }
            else {
            s=s+v[i].toString()+"\n";
            }
        }
        return s;
    }
    
    public static String toString(double [][] M){
        String s="";
        int n=M.length;
        for (int i=0; i<n;i++){
            for (int j=0; j<M[i].length; j++){
                s=s+M[i][j]+" ";
                if (j==M[i].length-1){s=s+"\n";}
            }
        }
        return s;
    }
    
    public static String toString(int [][] M){
        String s="";
        int n=M.length;
        for (int i=0; i<n;i++){
            for (int j=0; j<M[i].length; j++){
                s=s+M[i][j]+" ";
                if (j==M[i].length-1){s=s+"\n";}
            }
        }
        return s;
    }
    
    public static String toString(BigInteger[] v){
        if (v==null){return "null";}
        String str="";
        for (int i=0; i<v.length; i++){
            str=str+v[i].toString()+" ";
        }
        return str;
    }
    
    public static String toString(int[] v){
        if (v==null){return "null";}
        String str="";
        for (int i=0; i<v.length; i++){
            str=str+v[i]+" ";
        }
        return str;
    }
    
    public static String toStringPlusOne(int[] v){
        if (v==null){return "null";}
        String str="";
        for (int i=0; i<v.length; i++){
            str=str+(v[i]+1);
            if (i<v.length-1){
                str=str+" ";
            }
        }
        return str;
    }
    

     public static BigInteger max(BigInteger a, BigInteger b){
        if (a.compareTo(b)>0){
            return a;
        }
        else {
            return b;
        }
    }
    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
        
    public static BigInteger[] sum(BigInteger[] a, BigInteger[] b){
        int n=Math.min(a.length,b.length);
        BigInteger[] c=new BigInteger[n];
        for (int i=0; i<n; i++){
            c[i]=a[i].add(b[i]);
        }
        return c;
    }
    
    public static BigInteger[] minzero(BigInteger[] a){
        int n=a.length;
        BigInteger[] c=new BigInteger[n];
        for (int i=0; i<n; i++){
            if (BigInteger.ZERO.compareTo(a[i])>0){
                c[i]=new BigInteger(a[i].toString());
            }
            else {
                c[i]=BigInteger.ZERO;
            }
        }
        return c;
    }
    
    public static boolean isLessOrEqual(BigInteger[] a, BigInteger[] b){
        boolean res=true;
        for (int i=0; i<a.length; i++){
            if (a[i].compareTo(b[i])>0) {res=false;};
        }
        return res;
    }

    public static int[] invperm(int[] p){
        int[] q=new int[p.length];
        int i,j;
        //System.out.println("Direct: "+ permToString(p));
        for (i=0; i<p.length; i++){
            j=0;
            while ((j<p.length) && (p[j]!=i)){
                j++;
                }
            q[i]=j;
        }
        //System.out.println("Inverse: "+permToString(q));
        return q;
    }
    
    public static int[] compperm(int[] p1, int[] p2){
        int[] q=new int[p1.length];
        if (p1.length!=p2.length){return null;}
        for (int i=0; i<p1.length; i++){
            q[i]=p1[p2[i]];
        }
        return q;
    }
}


class SoundFilter extends javax.swing.filechooser.FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.au)||
                extension.equals(Utils.wav)||
                extension.equals(Utils.mid)||
                extension.equals(Utils.aif)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "*.au, *.wav, *.mid, *.aif";
    }
}

/* ImageFilter.java is a 1.4 example used by FileChooserDemo2.java. */
class MutationFilter extends javax.swing.filechooser.FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.qmu)){
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "*.qmu";
    }
}


class QuiverDrawing extends JPanel implements MouseListener, WindowListener,
   MouseMotionListener, ActionListener, ComponentListener, Printable {

    Frame fr;
    Seed S;
    QuiverSet qs;
    BMatrix LamQuiverDrawing;
    //Quiver q0;
    MoveablePoint movingPoint;
    int sourceindex;
    int firstMergePoint;
    Color newNodeColor;
    int chosenArrowStyle;
    JMenuItem showLabelsItem, showFrozenVerticesItem, gridItem, heightItem, 
        fontSizeItem, showNumbersItem, shortNumbersItem, lambdaQuiverItem, 
        CartanItem, CentralChargeItem, SubmodulesItem, SpikesItem, trafficLightsItem;
    JCheckBoxMenuItem MutSoundItem, BkgrSoundItem, ReplayItem, RandomItem;        
    JMenu clusterMenu, toolMenu, deactivateMenu;
    JLabel statusLabel;
    JSplitPane splitPane;
    boolean hasGrid;
    boolean heightsBeingShown;
    int status;
    JButton backbutton, forwardbutton, replaybutton1, replaybutton2;
    JFileChooser fc, soundfc;
    String fileName;
    String lastReadLine;
    PrinterJob printJob=null;
    PrintService ps=null; 
    int applType;
    Random MyRandom;
    int randnber;
    Long MySeed;
    int nodenber;
    int[] nodeseq;
    int gridsize;
    int friezenber;
    
    
    //BMatrix dimVect;
    int meshlength;
    boolean[] tiltingSummand;
    
    //int[] simples;
    
    javax.swing.Timer lapsetimer, muttimer, replayTimer, randomTimer;
   
    int edgeLength, timeLapse;
    
    SkewReflection mySkewReflection;
    Tracker myTracker;
    QXvariable myQxv;
    
    boolean mutsound, bkgrsound;
    AudioClip mutClip, bkgrClip;
    int replaydir, replayvertex, replayphase, replayspeed, replaypause;
    
    public static final int MUTATING=0;
    public static final int DONE=1;
    public static final int ADD_NODES=2;
    public static final int WAIT_FOR_NEW_SOURCE=3;
    public static final int WAIT_FOR_NEW_TARGET=4;
    public static final int DEL_NODES=5;
    public static final int FREEZE_NODES=6;
    public static final int WAIT_FOR_NEW_VSOURCE=7;
    public static final int WAIT_FOR_NEW_VTARGET=8;
    public static final int WAIT_FOR_NEXT_NODE=9;
    public static final int WAIT_FOR_TILTING_SUMMAND=11;
    public static final int WAIT_FOR_EXC_SUMMAND=12;
    public static final int MODIFYING_CENTRAL_CHARGE=13;
    public static final int WAIT_FOR_MERGE_1=14;
    public static final int WAIT_FOR_MERGE_2=15;
    public static final int ENTER_W_QUIVER=16;
    public static final int ENTER_DOUBLE_W_QUIVER=17;
    public static final int COLOR_NODES=18;
    public static final int WAIT_FOR_STYLE_SOURCE=19;
    public static final int WAIT_FOR_STYLE_TARGET=20;
    public static final int WAIT_FOR_BANFF_BRANCH_1=21;
    public static final int WAIT_FOR_BANFF_BRANCH_2=22;
    
    
    public static final int APPLET=0;
    public static final int APPLICATION=1;
    public static final int WSAPPLICATION=2;
   
    public void windowClosing(WindowEvent e) {
        //This will only be seen on standard output.
        //System.out.println("WindowListener method called: windowClosing.");
       
    }
    public void windowClosed(WindowEvent e) {
        //This will only be seen on standard output.
        //System.out.println("WindowListener method called: windowClosed.");
        if (bkgrsound){bkgrClip.stop();}
                  if (mutsound){mutClip.stop();}
		  System.exit(1);
    }

    public void windowOpened(WindowEvent e) {
        //System.out.println("WindowListener method called: windowOpened.");
    }

    
    public void windowIconified(WindowEvent e) {
        //System.out.println("WindowListener method called: windowIconified.");
    }

    public void windowDeiconified(WindowEvent e) {
        //System.out.println("WindowListener method called: windowDeiconified.");
    }

    public void windowActivated(WindowEvent e) {
        //System.out.println("WindowListener method called: windowActivated.");
    }

    public void windowDeactivated(WindowEvent e) {
        //System.out.println("WindowListener method called: windowDeactivated.");
    }

    public void windowGainedFocus(WindowEvent e) {
        //System.out.println("WindowFocusListener method called: windowGainedFocus.");
    }

    public void windowLostFocus(WindowEvent e) {
        //System.out.println("WindowFocusListener method called: windowLostFocus.");
    }

    public void windowStateChanged(WindowEvent e) {
        //System.out.println("WindowStateListener method called: windowStateChanged.");
    }
     

public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        if (qs==null){
            //System.out.println("qs is null.");
            if (pi >= 1) {
                return Printable.NO_SUCH_PAGE;
            }
         }
         else if ((pi>0) && (pi>qs.getnber()-1)){
            //System.out.println("qs is not null. pi is "+pi);
            //System.out.println("qs.getnber() is "+qs.getnber());
            return Printable.NO_SUCH_PAGE;
         }
	Graphics2D g2dall = (Graphics2D) g;
        Graphics2D g2dtop, g2dbottom;
        
        Rectangle rc=g2dall.getClipBounds();
        rc.setLocation(0,0);
        if (S.C==null){g2dtop=g2dall;}
        else {
            g2dtop=(Graphics2D) g2dall.create(0,0,rc.width, rc.height/2);
            g2dbottom=(Graphics2D) g2dall.create(0, rc.height/2, rc.width, rc.height/2);
            
            
            int fz=S.dPane.getFontSize();
            S.dPane.setFontSize(14);
            String str=S.dPane.getText();
            
            //S.dPane.setText(S.C.toString());
            rc=g2dbottom.getClipBounds();
            Rectangle rq=S.dPane.getBounds();
            rq.setLocation(0,0);
        
            //printrect(g2d,rc);
        
            double cw=rc.getWidth()/rq.getWidth();
            double ch=rc.getHeight()/rq.getHeight();
            double c=cw;
            if (ch<=cw){c=ch;}
        
            double gtx= rc.getCenterX()-c*rq.getCenterX();
            double gty= rc.getCenterY()-c*rq.getCenterY();
        
            g2dbottom.translate(gtx,gty);
            g2dbottom.scale(c,c);
            
            S.dPane.print(g2dbottom);
            S.dPane.setText(str);
            S.dPane.setFontSize(fz);
        }
        
        rc=g2dtop.getClipBounds();
        Rectangle rq=this.getBounds();
        rq.setLocation(0,0);
        
        //printrect(g2d,rc);
        
        double cw=rc.getWidth()/rq.getWidth();
        double ch=rc.getHeight()/rq.getHeight();
        double c=cw;
        if (ch<=cw){c=ch;}
        
        double gtx= rc.getCenterX()-c*rq.getCenterX();
        double gty= rc.getCenterY()-c*rq.getCenterY();
        
        g2dtop.translate(gtx,gty);
        g2dtop.scale(c,c);
    
        //printrect(g2d,rq);

        if (qs==null){
	  S.Q.drawQuiver(g2dtop);
          //System.out.println("qs is null. Drawing quiver via S.Q.drawQuiver(g2dtop)");
        }

        if (qs!=null){
           if (qs.getnber() <= 1) {
                S.Q.drawQuiver(g2dtop);
                //System.out.println("qs is not null. Drawing quiver via S.Q.drawQuiver(g2dtop)");
           }
           Quiver q;
           if (qs.getnber()>1) {
              q=qs.getelementAt(pi).getQuiver(this);
              q.setgrowthFactor((float) 0.1);
              q.scalecenter(getBounds());
              q.drawQuiver(g2dtop);
              if (q.tauorder>=-1){
                  g2dtop.setPaint(Color.black);
                  Font oldft=g2dtop.getFont();
                  Font newft=oldft.deriveFont((float) 30);
                  g2dtop.setFont(newft);
                  FontMetrics fm = g2dtop.getFontMetrics();
                  String s=""+(pi+1)+": ";
                  if (q.tauorder>-1){
                      s=s+q.tauorder;}
                  else {
                      s=s+"inf";
                  }
                  Rectangle2D rect = fm.getStringBounds(s, g2dtop);
                  double w=rect.getWidth();
                  double h=rect.getHeight();
                  g2dtop.drawString(s, (float) 0, (float) h);
                  g2dtop.setFont(oldft);
               }
              System.out.println("Printing quiver number "+(pi+1));
           }
        }
	return Printable.PAGE_EXISTS;
    }

    
public void componentHidden(ComponentEvent e) {
        //System.out.println("componentHidden event from "
        //               + e.getComponent().getClass().getName());
    }

    public void componentMoved(ComponentEvent e) {
        Component c = e.getComponent();
        /*System.out.println("componentMoved event from "
                       + c.getClass().getName() 
                       + "; new location: "
                       + c.getLocation().x
                       + ", "
                       + c.getLocation().y);
         */
    }

    public void componentResized(ComponentEvent e) {
        Component c = e.getComponent();
        /*System.out.println("componentResized event from "
                       + c.getClass().getName()
                       + "; new size: "
                       + c.getSize().width
                       + ", "
                       + c.getSize().height);
         */
        S.Q.scalecenter(getBounds());
        repaint();
        
    }

    public void componentShown(ComponentEvent e) {
        /*System.out.println("componentShown event from "
                       + e.getComponent().getClass().getName());
         */
    }

    public void updatehistorybuttons(int cter, int size){
        //System.out.println("updatebuttons: historycounter:"+ cter);
        //System.out.println("size:"+size);
	if (cter>-1){
	     backbutton.setEnabled(true);}
	else {
	     backbutton.setEnabled(false);}
	if (cter<size-1){
	     forwardbutton.setEnabled(true);}
	 else {
	     forwardbutton.setEnabled(false);}
        if ((status==DEL_NODES)&&(S.Q.remembersOldQuiver())){
            backbutton.setEnabled(true);
        }
    }

    public void updatetoolmenu(){
	for (int i=24; i<33; i++){
	    toolMenu.getItem(i).setEnabled(false);
	}
	if (qs!=null){
	if (qs.size()>0){
	    for (int i=23; i<33; i++){
	    toolMenu.getItem(i).setEnabled(true);
	    }
	}
	}
        if (lapsetimer!=null){
            toolMenu.getItem(20).setText("Static quivers");
        }
        else {
            toolMenu.getItem(20).setText("Live quivers");
        }
    }

    public void updatestatus(int st){
        updatestatus("",st);
    }

    public void updatestatus(String msg, int st){
        String s=msg;
        if (qs.getnber()>1){
            s=""+(qs.getcter()+1)+"/"+(qs.getnber())+". ";
        }
        if (S.Q.tauorder>-1){
            s=s+"Tau-order="+S.Q.tauorder+". ";
        }
        status=st;
        switch (st){
    case MODIFYING_CENTRAL_CHARGE:
                statusLabel.setText(s+"Drag pink nodes representing simples"); break;
    case MUTATING:              
        statusLabel.setText(s+"Click or drag nodes"); break;
    case WAIT_FOR_BANFF_BRANCH_1:
        statusLabel.setText(s+"Click on first Banff branch vertex"); break;
    case WAIT_FOR_BANFF_BRANCH_2:
        statusLabel.setText(s+"Click on second Banff branch vertex"); break;
    case ADD_NODES: 
        /*for (int i=0; i<clusterMenu.getItemCount();i++){
		    clusterMenu.getItem(i).setEnabled(false);
        }*/
        statusLabel.setText(s+"Click to create new nodes"); break;
    case ENTER_W_QUIVER:
        /*for (int i=0; i<clusterMenu.getItemCount();i++){
		    clusterMenu.getItem(i).setEnabled(false);
        }*/
        statusLabel.setText(s+"Click to add factors to word or press Done"); break;
    case ENTER_DOUBLE_W_QUIVER:
        /*for (int i=0; i<clusterMenu.getItemCount();i++){
		    clusterMenu.getItem(i).setEnabled(false);
        }*/
        statusLabel.setText(s+"Click or alt-Click to add factors to double word or press Done"); break;
    case DONE: 
        statusLabel.setText(s+"Done pressed"); break;
    case WAIT_FOR_NEW_SOURCE:
        statusLabel.setText(s+"Click on source of new arrow or press Done"); 
            break;
    case WAIT_FOR_NEW_TARGET:
        statusLabel.setText(s+"Click on target of new arrow"); break;
   
    case DEL_NODES:
        /*for (int i=0; i<clusterMenu.getItemCount();i++){
		    clusterMenu.getItem(i).setEnabled(false);
        }*/
        statusLabel.setText(s+"Click on node to delete or press Done"); 
            break;
    case FREEZE_NODES:
        statusLabel.setText(s+"Click on node to (un)freeze or press Done"); 
            break;
    case COLOR_NODES:
        statusLabel.setText(s+"Click on node to color it or press Done"); 
            break;
    case WAIT_FOR_NEW_VSOURCE:
        statusLabel.setText(s+"Click on source of new valued arrow or press Done"); 
            break;
    case WAIT_FOR_NEW_VTARGET:
        statusLabel.setText(s+"Click on target of new valued arrow"); break;            
    case WAIT_FOR_STYLE_SOURCE:
        statusLabel.setText(s+"Click on source of arrow to be styled or press Done"); 
            break;
    case WAIT_FOR_STYLE_TARGET:
        statusLabel.setText(s+"Click on target of arrow to be styled or press Done"); 
            break;
    case WAIT_FOR_NEXT_NODE:
        statusLabel.setText(s+"Click on new node number "+(nodenber+1)+ ". Click Done to cancel."); break;     
    case WAIT_FOR_MERGE_1:
    statusLabel.setText(s+"Click on first vertex to merge or press Done"); 
        break;
    case WAIT_FOR_MERGE_2:
    statusLabel.setText(s+"Click on second vertex to merge or press Done"); 
        break;
        }
    }

    
    private void WSOpen(){
        
        FileOpenService fos = null;
            FileContents fileContents = null;

            try {
                fos = (FileOpenService)ServiceManager.
                          lookup("javax.jnlp.FileOpenService"); 
            } catch (UnavailableServiceException exc) {
            System.out.println(exc.getMessage());}
            
            if (fos != null) {
                System.out.println("fos non null.");
                try {
                    fileContents = fos.openFileDialog(null, new String[] { "qmu" } ); 
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(this, "Open command failed: "
                               + exc.getLocalizedMessage() + "\n");
                }
            }

            if (fileContents!=null){
		try{
		    InputStream in=fileContents.getInputStream();
		    //  JOptionPane.showMessageDialog(this, "Now before creating InputStreamReader");
		    InputStreamReader isr=new InputStreamReader(in);
		    // JOptionPane.showMessageDialog(this, "Now before creating BufferedReader");
            	    BufferedReader r=new BufferedReader(isr);
		    S.read(r);
		    updatePaneAndMenu();
		    S.Q.Hist.updatebuttons();
                    S.Q.scalecenter(getBounds());
		    r.close();
		 }
                 catch (IOException e){
                        System.out.println("I/O exception: "+ e.getMessage());
                 }
 
                if (S.Q.showLabels){
                    showLabelsItem.setText("Hide labels");}
                else {
                    showLabelsItem.setText("Show labels");}
                
                if (S.Q.showFrozenVertices){
                    showFrozenVerticesItem.setText("Hide frozen vertices");}
                else {
                    showFrozenVerticesItem.setText("Show frozen vertices");
                }
                
                if (S.Q.trafficLights){
                    trafficLightsItem.setText("Switch traffic lights off");}
                else {
                    trafficLightsItem.setText("Switch traffic lights on");
                }


	    }
    }
    
    public void setLastReadLine(String str){
            lastReadLine=str;
    }
    
    private AudioClip OpenSound(){
        AudioClip myClip=null;
        int returnVal=soundfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = soundfc.getSelectedFile();
            System.out.println("Filename:"+file.getName());
            URL ClipURL=null;
            try {
                ClipURL = file.toURL();
            } catch (MalformedURLException e){
                System.err.println(e.getMessage());
            }
            System.out.println("ClipURL:"+ClipURL);
            myClip=Applet.newAudioClip(ClipURL);
            }
        return myClip;
        }
    
    void Add(){
        BMatrix M=new BMatrix(1,1);
        M.makeZero();
        System.out.println("Dummy matrix:"+ M.toString());
        Quiver addQ=new Quiver(M,this);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fileName=file.getName();
                //System.out.println("Opening: " + file.getName() + ".");
                FileReader in=null;
                try{
                    in = new FileReader(file);
                    }
                catch (FileNotFoundException e) {
                    System.out.println("File not found: "+e.getMessage()); 
                }

                if (in!=null){
		   BufferedReader r=null;
		   r= new BufferedReader(in);
                    try{
			String str;
                        //System.out.println("About to read seed.");
                        addQ.read(r);
			r.close();
                        lastReadLine=null;
                        System.out.println("Quiver to add:"+addQ.toString());
                        S.Q.addQuiver(addQ);
		    }
                    catch (IOException e){
                        System.out.println("I/O exception: "+ e.getMessage());
                    }
		}
        }
       else {
            System.out.println("Add command cancelled by user.");
       }
    }

    void Unite(){
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fileName=file.getName();
                //System.out.println("Opening: " + file.getName() + ".");
                FileReader in=null;
                try{
                    in = new FileReader(file);
                    }
                catch (FileNotFoundException e) {
                    System.out.println("File not found: "+e.getMessage()); 
                }
                QuiverSet qs1=new QuiverSet(this);
                
                if (in!=null){
		   BufferedReader r=null;
		   r= new BufferedReader(in);
                    try{
			String str;
                        //System.out.println("About to read seed.");
			S.read(r);
                        //System.out.println("Seed read.");
			updatePaneAndMenu();
                        S.Q.scalecenter(getBounds());
                        //System.out.println("Rescaling done.");
                        //qs.clear();
                        qs1.read(r,this);
                        //System.out.println("Quiverset read.");
                        qs1.insertLinkToQuiver(S.Q);
			r.close();
		    }
                    catch (IOException e){
                        System.out.println("I/O exception: "+ e.getMessage());
                    }
                   for (int i=0;i<qs1.getnber();i++) {
                       qs.append(qs1.elementAt(i));
                   } 
                   
                   S.Q.Hist.updatebuttons();
                   updatestatus(status);
                   updatetoolmenu();
		}
                fr.setTitle("Quiver mutation: "+fileName);
	    }
         else {
                System.out.println("Open command cancelled by user.");
         }
        QuiverSetSorter.sort(qs,2);
            S.Q=qs.first().getQuiver(this);
            //System.out.println("Finished sorting the quivers.");
            //System.out.println("Quiver taumutseq: "+S.Q.taumutseq);
            S.Q.Hist.updatebuttons();
    }
    
    void Minus(){
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fileName=file.getName();
                //System.out.println("Opening: " + file.getName() + ".");
                FileReader in=null;
                try{
                    in = new FileReader(file);
                    }
                catch (FileNotFoundException e) {
                    System.out.println("File not found: "+e.getMessage()); 
                }
                QuiverSet qs1=new QuiverSet(this);
                AbstractQuiver aq1=null;
                AbstractQuiver aq=null;
                
                if (in!=null){
		   BufferedReader r=null;
		   r= new BufferedReader(in);
                    try{
			String str;
                        //System.out.println("About to read seed.");
			S.read(r);
                        //System.out.println("Seed read.");
			updatePaneAndMenu();
                        S.Q.scalecenter(getBounds());
                        //System.out.println("Rescaling done.");
                        //qs.clear();
                        qs1.read(r,this);
                        //System.out.println("Quiverset read.");
                        qs1.insertLinkToQuiver(S.Q);
			r.close();
		    }
                    catch (IOException e){
                        System.out.println("I/O exception: "+ e.getMessage());
                    }
                   for (int i=0;i<qs1.getnber();i++) {
                       aq1=qs1.elementAt(i);
                       int j=0;
                       while (j<qs.getnber()){
                           aq=qs.elementAt(j);
                           if ((aq.equals(aq1)) & (qs.getnber()>1)){
                               qs.v.remove(j);
                               System.out.println("Removing quiver "+j);
                               if (qs.getnber()==0){
                                   qs.add(AbstractQuiver.emptyAbstractQuiver());
                               }
                           }
                           j++;          
                       }
                   } 
                   
                   S.Q.Hist.updatebuttons();
                   updatestatus(status);
                   updatetoolmenu();
		}
                fr.setTitle("Quiver mutation: "+fileName);
	    }
         else {
                System.out.println("Open command cancelled by user.");
         }
        //QuiverSetSorter.sort(qs,2);
            S.Q=qs.first().getQuiver(this);
            //System.out.println("Finished sorting the quivers.");
            //System.out.println("Quiver taumutseq: "+S.Q.taumutseq);
            S.Q.Hist.updatebuttons();
    }
    
    private void Open(){
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fileName=file.getName();
                //System.out.println("Opening: " + file.getName() + ".");
                FileReader in=null;
                try{
                    in = new FileReader(file);
                    }
                catch (FileNotFoundException e) {
                    System.out.println("File not found: "+e.getMessage()); 
                }

                if (in!=null){
		   BufferedReader r=null;
		   r= new BufferedReader(in);
                    try{
			String str;
                        //System.out.println("About to read seed.");
			S.read(r);
                        //System.out.println("Seed read.");
			updatePaneAndMenu();
                        S.Q.scalecenter(getBounds());
                        //System.out.println("Rescaling done.");
                        qs.clear();
                        qs.read(r,this);
                        //System.out.println("Quiverset read.");
                        qs.insertLinkToQuiver(S.Q);
			r.close();
		    }
                    catch (IOException e){
                        System.out.println("I/O exception: "+ e.getMessage());
                    }
                   S.Q.Hist.updatebuttons();
                   updatestatus(status);
                   updatetoolmenu();
		}
                fr.setTitle("Quiver mutation: "+fileName);
	}
         else {
                System.out.println("Open command cancelled by user.");
            }
    }


    private void WSSave(){
        
        FileSaveService fss = null;
        FileContents fileContents = null;
        try {
                fss = (FileSaveService)ServiceManager.
                          lookup("javax.jnlp.FileSaveService"); 
            } catch (UnavailableServiceException exc) {
                JOptionPane.showMessageDialog(this, exc.getMessage());
            }
        if (fss != null) {
            try {
                StringBufferInputStream is = new StringBufferInputStream(
                                             "Saved by JWSFileChooserDemo");
                fileContents = fss.saveFileDialog(null,
                                                  new String[] { "qmu" },
                                                  is,
                                                  "JWSFileChooserDemo.txt"); 
            } 
            catch (Exception exc) {
                    JOptionPane.showMessageDialog(this,"Save command failed: "
                               + exc.getLocalizedMessage());
            }
        }
        if (fileContents != null) {
		ObjectOutputStream s=null;
                try{
		    long lg=128000;
		    fileContents.setMaxLength(lg);
                    //JOptionPane.showMessageDialog(this,"Max. file length:"+fileContents.getMaxLength());
                    s = new ObjectOutputStream(fileContents.getOutputStream(true));
		    OutputStream out=fileContents.getOutputStream(true);
		    //JOptionPane.showMessageDialog(this, "Now before creating OutputStreamReader");
		    OutputStreamWriter osw=new OutputStreamWriter(out);
		    //JOptionPane.showMessageDialog(this, "Now before creating BufferedWriter");
            	    BufferedWriter w=new BufferedWriter(osw);
		 
		    S.write(w);
		    w.flush();
		       
		    }
                catch (IOException exc) {
                    JOptionPane.showMessageDialog(this,"Problem saving file: "
                               + exc.getMessage());
                }
	} else {
            JOptionPane.showMessageDialog(this,"User canceled save request." );
	}
     }
    
    private void SaveIt(){
        if (fc==null){return;}
        File file = fc.getSelectedFile();
        if (file==null){return;}
        fileName=file.getName();
        //System.out.println("Saved "+fileName);
        FileWriter out=null;
        try{
            out = new FileWriter(file);
            }
        catch (IOException e) {
            System.out.println("File not found."); 
        }

        BufferedWriter w=null;
        try{
            if (out!=null){
            w = new BufferedWriter(out);
            S.write(w);
            if (qs.size()>0){
                qs.write(w,this);
            }
            w.flush();
            }
        }
        catch (IOException e){
            System.out.println("I/O Exception:"+ e.getMessage());
        }
        fr.setTitle("Quiver mutation: "+fileName);
    }
    
    private void Save(){
        if (fileName.equals("")){
            SaveAs();
        }
        else 
        {
            SaveIt();
        } 
    }

    private void SaveAs(){
        File file=null;
        
        int ans=JOptionPane.YES_OPTION;
        int returnVal;
        do {
            returnVal = fc.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                //System.out.println("Saving: " + file.getName() + ".");
                if (file.exists()){
                    ans=JOptionPane.showConfirmDialog(this,"File exists. Replace ?");
                }
            }
        } while ((ans==JOptionPane.NO_OPTION) & (returnVal!=JFileChooser.CANCEL_OPTION));
        if (ans==JOptionPane.YES_OPTION){
            SaveIt();
        }
     }
    
    private void WSPrint(){
        if (ps==null){
        try { 
            //JOptionPane.showMessageDialog(this, "ps is null.");
            ps = (PrintService)ServiceManager.lookup("javax.jnlp.PrintService"); 
        } catch (UnavailableServiceException e) { 
            ps = null; 
        } 
        }

        if (ps != null) { 
            try { 
                //JOptionPane.showMessageDialog(this, "ps is not null.");
                // get the default PageFormat
                //PageFormat pf = ps.getDefaultPage(); 
                // ask the user to customize the PageFormat
                //PageFormat newPf = ps.showPageFormatDialog(pf); 
                //JOptionPane.showMessageDialog(this, "Now just before print");
                // print the document with the PageFormat above
                ps.print(this); 

            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        }
   }
    

    private void Print(){
	//if (printJob!=null){
	if (false){
             try {
                printJob.print();  
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
         }
         else {
	     //System.out.println("printJob is null.");
         printJob = PrinterJob.getPrinterJob();
         printJob.setPrintable(this);
         if (printJob.printDialog()) {
            try {
                printJob.print();  
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
         }
        }
     }

    private void updatePaneAndMenu(){
            
	if (S.C!=null){          
            fontSizeItem.setEnabled(true);
            showNumbersItem.setEnabled(true);
            
            if (splitPane.getBottomComponent()==null){
                splitPane.setBottomComponent(S.dPane);
                splitPane.setDividerLocation(0.8);
            }
            S.updateDPane();
            //System.out.println("Divider location:" + splitPane.getDividerLocation());
    
            if (S.C.getShowNumbers()){
                showNumbersItem.setText("Hide numbers");
            }
            else {
                showNumbersItem.setText("Show numbers");
            }
            
            clusterMenu.remove(deactivateMenu);
            deactivateMenu=null;
            deactivateMenu=S.C.getDeactivateMenu(this);
            clusterMenu.add(deactivateMenu);
            
	    //for (int i=3; i<clusterMenu.getItemCount();i++){
		//clusterMenu.getItem(i).setEnabled(true);
	    //}
	}
	 else{
                S.deactivateDPane();
                splitPane.setBottomComponent(null);
		//splitPane.setDividerLocation(0.999);
                
                fontSizeItem.setEnabled(false);
                showNumbersItem.setEnabled(false);
                
                clusterMenu.remove(deactivateMenu);
                deactivateMenu=null;
                deactivateMenu=new JMenu("Deactivate");
                clusterMenu.add(deactivateMenu);
                
		/*for (int i=3; i<clusterMenu.getItemCount();i++){
		    clusterMenu.getItem(i).setEnabled(false); 
		}*/
	 }
    }
        
private void makeLighter(int nber){
    randnber=nber;
        BMatrix Mrun=new BMatrix(S.Q.nbpoints,S.Q.nbpoints);
        Mrun.copyfrom(S.Q.M);
        BMatrix Mmin=new BMatrix(S.Q.nbpoints,S.Q.nbpoints);
        Mmin.copyfrom(S.Q.M);
        BigInteger minweight=Mmin.weight();
        BigInteger runweight=Mrun.weight();
        BigInteger overflow=new BigInteger("1000000000");
        int mini=0;
        int i=0;
        while ((i<=nber)& (runweight.compareTo(overflow)<0)){
            int r=MyRandom.nextInt(S.Q.nbpoints);
            Mrun.mutate(r);
            runweight=null;
            runweight=Mrun.weight();
            if (runweight.compareTo(minweight)<=0){
                mini=i;
                Mmin.copyfrom(Mrun);
                minweight=null;
                minweight=new BigInteger(runweight.toString());
                //System.out.println("Iteration: "+i+", Weight: "+ minweight);
            }
            i++;
            //System.out.println("Iteration: " +i + ", Weight: "+ runweight);
        }
        if (runweight.compareTo(overflow)>=0){
            JOptionPane.showMessageDialog(this,"Computation aborted after "+ i + " iterations.\n"+
                    "Last weight attained: "+runweight);
        }
        JOptionPane.showMessageDialog(this,"Minimal weight found: "+minweight+
                "\n"+"Iterations: "+mini);
        S.Q.M.copyfrom(Mmin);
}
   

    public static BigInteger factorial (BigInteger n){
        if (n.equals(BigInteger.ONE)){
            return n;
        }
        else {
            return n.multiply(factorial(n.subtract(BigInteger.ONE)));
        }
    }
    
    public void setedgeLength(int l){
        edgeLength=l;
    }
    
    public void settimeLapse(int l){
        timeLapse=l;
        if (lapsetimer!=null){
            lapsetimer.setDelay(timeLapse);
        }
    }
    
    public void actionPerformed(ActionEvent event) {
    
    String actionCommand = event.getActionCommand();
    char lastchar=' ';
    if (actionCommand!=null){
        lastchar=actionCommand.charAt(actionCommand.length()-1);
        //System.out.println(actionCommand+", Last Char:"+lastchar);
    }
    
    if (actionCommand==null){
        if (event.getSource()==lapsetimer){
        S.Q.relax(edgeLength);
        }
        if (event.getSource()==muttimer){
            int r=MyRandom.nextInt(S.Q.nbpoints);
               S.mutate(r);
               S.Q.Hist.add(r);
        }
        if (event.getSource()==replayTimer) {
            if (replaypause>0){
                replaypause--;
            }
            else {
                //System.out.println("ShakeTimer triggered this print event.");
                /*int i=(int)(Math.random() * S.Q.nbpoints);
                //System.out.println("i="+i);
		MoveablePoint P1 = S.Q.P[i];
		if (!P1.fixed) {
		    P1.x += 100*Math.random() - 50;
		    P1.y += 100*Math.random() - 50;
                    System.out.println("Point modified:"+P1);
		}
		//graph.play(graph.getCodeBase(), "audio/drip.au");
                 */
                
                if (replayvertex!=History.END_OF_HISTORY){
                    S.Q.P[replayvertex].setColor(Color.RED);
                }
                
                if ((replaydir==-1)&&(!backbutton.isEnabled())&&(forwardbutton.isEnabled())){
                    replaydir=1;
                    replayphase=0;
                    replaypause=3;
                    replayvertex=S.Q.Hist.next();
                }
                
                if ((replaydir==1)&&(backbutton.isEnabled())&&(!forwardbutton.isEnabled())){
                    replaydir=-1;
                    replayphase=0;
                    replaypause=3;
                    replayvertex=S.Q.Hist.present();
                }
                
                if ((!backbutton.isEnabled())&&(!forwardbutton.isEnabled())){
                    replaydir=0;
                }
                
                if (replayphase==1){
                    if (replaydir==-1){
                        int mutvert=S.Q.Hist.back();
                        if (mutvert!=History.END_OF_HISTORY){
                           if (mutsound){mutClip.play();}
                           S.mutate(mutvert);}
                        replayvertex=S.Q.Hist.present();
                    }

                    if (replaydir==1){
                       int mutvert=S.Q.Hist.forward();
                       if (mutvert!=History.END_OF_HISTORY){
                            if (mutsound){mutClip.play();}
                            S.mutate(mutvert);
                        }
                       replayvertex=S.Q.Hist.next();
                    }
                }
                
                if (replaypause==0){
                    if (replayphase==0){
                        if (replayvertex!=History.END_OF_HISTORY){
                                S.Q.P[replayvertex].setColor(Color.GREEN);
                        }
                    }

                    replayphase++;
                    if (replayphase>1){replayphase=replayphase-2;}
                }
            }
        }
        
        
        if (event.getSource()==randomTimer) {
            if (S.C!=null) {
             if (S.C.getLastMutableOfType("gr-vectors")!=null){
                for (int i=0; i<S.Q.nbpoints; i++){
                    if (S.Q.P[i].getColor()==Color.GREEN){
                        S.mutate(i);
                        S.Q.Hist.add(i);
                        continue;
                    }
                }
            } 
            }
            else 
            {
                if (replayvertex!=History.END_OF_HISTORY){
                    S.Q.P[replayvertex].setColor(Color.RED);
                }
                
                if (replayphase==1){
                    if (mutsound){mutClip.play();}
                    S.mutate(replayvertex);
                    replayvertex=(int)(Math.random() * S.Q.nbpoints);
                }
                
                if (replayphase==0){
                    if (replayvertex!=History.END_OF_HISTORY){
                            S.Q.P[replayvertex].setColor(Color.GREEN);
                    }
                }
                
                replayphase++;
                if (replayphase>1){replayphase=replayphase-2;}
            }   
        }
    }
    
    else if (lastchar==')'){
        //System.out.println(actionCommand);
        S.removeMutable(actionCommand);
        updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("All")){
        S.removeAllMutables();
        updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("Reset")) {
        showLabelsItem.setText("Show labels");     
        showFrozenVerticesItem.setText("Hide frozen vertices");
        trafficLightsItem.setText("Switch traffic lights on");
        S.Q.Hist.reset();
        S.reset(S.Q.parameter, this);
        updatePaneAndMenu();
     }
    
     else if (actionCommand.equals("Quit")){
                  if (bkgrsound){bkgrClip.stop();}
                  if (mutsound){mutClip.stop();}
		  System.exit(1);
	      }
    else if (actionCommand.equals("New quiver")||actionCommand.equals("New quiver ...")){
        String s=(String)JOptionPane.showInputDialog("Enter side length:", "4");
        if (s!=null){
            int t=Integer.parseInt(s);
            qs.clear();
            updatetoolmenu();
            showLabelsItem.setText("Show labels") ;
            showFrozenVerticesItem.setText("Hide frozen vertices");
            trafficLightsItem.setText("Switch traffic lights on");
            S.Q.Hist.reset();
            S.Q.Hist.updatebuttons();
            S.Q.Oxvector=null;
            if (S.Q.SeqDia!=null){S.Q.SeqDia.dispose(); S.Q.SeqDia=null;}
            S.reset(t,this);
            updatePaneAndMenu();
            updatestatus(MUTATING);
            fileName="";
            fr.setTitle("Quiver mutation");
        }
    }
    else if (actionCommand.equals("Done")){
        /*for (int i=0; i<2;i++){
		    clusterMenu.getItem(i).setEnabled(true);
        }*/
        
        if (status==WAIT_FOR_NEXT_NODE){
             for (int i=0; i<S.Q.nbpoints; i++){
                 S.Q.P[i].marked=false;
                 S.Q.P[i].deleteLabel();
                }
             if (heightsBeingShown){
                 S.Q.showHeights();
             }
        }
        
        if (status==ENTER_DOUBLE_W_QUIVER){
            hasGrid=false;
            gridItem.setText("Show grid");
            S.Q.finalizeDoubleWordquiver();
        }
        
        if (status==ENTER_W_QUIVER){
            hasGrid=false;
            gridItem.setText("Show grid");
            S.Q.finalizeWordquiver();

            
            SequencesDialog sd=new SequencesDialog(this, false);
            String taumutseq=S.Q.getWordquiverTaumutseq();
            String tauperm=S.Q.getWordquiverTauperm();
            sd.setSequence(taumutseq);
            sd.setPerm(tauperm);
            S.Q.setSeqDia(sd);
            int bound=100;

            int order=S.mutorder(taumutseq,tauperm,bound);
            
            if (order>-1){
                    JOptionPane.showMessageDialog(this,"Order="+order);
                    }
                else {
                   JOptionPane.showMessageDialog(this,"Order > "+bound);
                }
        }
        updatestatus(MUTATING);
        updatePaneAndMenu();
    }
    else if (actionCommand.equals("Banff branches")){
            updatestatus(WAIT_FOR_BANFF_BRANCH_1);
            }
    else if (actionCommand.equals("Add nodes")){
            S.removeAllMutables();
            updatePaneAndMenu();
            updatestatus(ADD_NODES);}
    else if (actionCommand.equals("Enter double w-quiver")){
            hasGrid=true;
            gridItem.setText("Hide grid") ;
            S.removeAllMutables();
            S.Q.initializeDoubleWordquiver();
            updatePaneAndMenu();
            updatestatus(ENTER_DOUBLE_W_QUIVER);}
    else if (actionCommand.equals("Enter w-quiver")){
            hasGrid=true;
            gridItem.setText("Hide grid") ;
            S.removeAllMutables();
            S.Q.initializeWordquiver();
            updatePaneAndMenu();
            updatestatus(ENTER_W_QUIVER);}
    else if (actionCommand.equals("Next w-quiver")){
            int i;
            int lastnonfrozen;
            do {
                lastnonfrozen=-1;
                for (i=0; i<S.Q.nbpoints; i++){
                    if (!S.Q.P[i].frozen){
                        lastnonfrozen=i;
                    }
                }
                if (lastnonfrozen>-1) {S.deletepoint(lastnonfrozen);}
            } while (lastnonfrozen>-1);
            
            for (i=0; i<S.Q.nbpoints; i++){
                S.Q.P[i].frozen=false;
            }
            hasGrid=true;
            gridItem.setText("Hide grid") ;
            S.Q.SeqDia.setVisible(false);
            S.Q.SeqDia=null;
            S.removeAllMutables();
            S.Q.initializeWordquiver();
            updatePaneAndMenu();
            updatestatus(ENTER_W_QUIVER);}
    else if (actionCommand.equals("Stretch")){
        S.Q.stretchcenter(getBounds());
    }
    else if (actionCommand.equals("Enumerate w-quivers")){
            Permutation P=new Permutation(S.Q.nbpoints+1);
            P=P.next();

            String s=JOptionPane.showInputDialog(this,"Enter number of w-quivers to be computed.");
            int nber=Integer.parseInt(s);
            
            
            int[] sq;
            double x,y;
            double tx=30;
            double ty=0;
            int i, lastnonfrozen, order;
            String[] seq;
            String msg;
            int bound=500;

            int orignbpoints=S.Q.nbpoints;
            //float c=(orignbpoints*(orignbpoints+1)/2)/2;

            float c=(float) orignbpoints;
            c=2*c;
            boolean isselected;

            qs=new QuiverSet(this);
            AbstractQuiver aq;
            Quiver Qswap;


            while (P!=null) {
                //System.out.println("3:qs.getnber()="+qs.getnber());
                //Permutation P=Permutation.rand(S.Q.nbpoints+1);
                //JOptionPane.showMessageDialog(this, "Permutation: "+ P+ "\nReduced word: "+P.redword());
                sq=Utils.StringToIntArray(P.redword());
                S.removeAllMutables();
                S.Q.initializeWordquiver();
                //double x,y;
                //double tx=30;
                //double ty=0;
                for (i=0; i<sq.length; i++){
                    x=S.Q.P[sq[i]].x+(i+1)*tx;
                    y=S.Q.P[sq[i]].y+(i+1)*ty;
                    S.addwpoint((int) Math.round(x), (int) Math.round(y));
                }
                S.Q.finalizeWordquiver();
                
                boolean isFaithful=true;
                boolean isIsolated;
                int iisol=-1;
                int j=0;
                for (i=0; i<S.Q.blueprintnbpoints;i++){
                    isIsolated=true;
                    for (j=S.Q.blueprintnbpoints; j<S.Q.nbpoints; j++){
                       if (S.Q.M.A[i][j].compareTo(BigInteger.ZERO)!=0){isIsolated=false;}
                    }
                    if (isIsolated) {
                        iisol=i;
                        isFaithful=false;
                    }
                }

                seq=new String[2];
                seq[0]=S.Q.getWordquiverTaumutseq();
                seq[1]=S.Q.getWordquiverTauperm();

                order=S.mutorder(seq[0],seq[1],bound);
                S.Q.tauorder=order;
                msg="";

                //System.out.println("Orignbpoints="+orignbpoints);
                //System.out.println("S.Q.nbpoints="+S.Q.nbpoints);

                //isselected=(S.Q.nbpoints>=c);
                isselected=true;

                if (isselected){
                        msg="Order="+order;
                        }

                //System.out.println(" order="+order);
                //if ((order>-1) & (sq.length>=2*P.n)){
                if ((isselected)){
                    //S.Q.scalecenter(getBounds());
                    
                    aq=new AbstractQuiver(S.Q, this, true);
                    aq.embeddedQuiver.scalecenter(getBounds());
                    //System.out.println("0:qs.getnber()="+qs.getnber());
                    //System.out.println("Is faithful:"+isFaithful);
                    //if ((isFaithful) && (order!=-1)) {qs.add(aq);}
                    if (isFaithful & (order!=-1) ) {qs.add(aq);}
                    //System.out.println("1:qs.getnber()="+qs.getnber());
                    //System.out.println("Adding quiver number "+ (qs.getnber()+1)+". Order="+S.Q.tauorder);
                    
                    Qswap=S.Q;
                    S.Q=aq.embeddedQuiver;
                    updatestatus(MUTATING);

                    //JOptionPane.showMessageDialog(null,msg);

                    S.Q=Qswap;
                }

                updatestatus(MUTATING);
                repaint();

                do {
                    lastnonfrozen=-1;
                    for (i=0; i<S.Q.nbpoints; i++){
                        if (!S.Q.P[i].frozen){
                            lastnonfrozen=i;
                        }
                    }
                    if (lastnonfrozen>-1) {S.deletepoint(lastnonfrozen);}
                } while (lastnonfrozen>-1);

                for (i=0; i<S.Q.nbpoints; i++){
                    S.Q.P[i].frozen=false;
                }
                S.Q.moveLeft();
                //S.Q.stretchcenter(getBounds());
                

                
                System.out.println("qs:"+qs.getnber()+" order="+order+" isFaithful="+isFaithful+" iisol="+iisol+ " P: " +P.toString());
                
                if (iisol!=0){
                    System.out.println("iisol="+iisol);
                }
                P=P.next();
                           
                if (qs.getnber()>=nber) {P=null;}
               
            }
            updatetoolmenu();
            
            if (qs!=null){
                if (qs.getnber()>0) {
                    //System.out.println("Sorting the quivers by tau-order.");
                    QuiverSetSorter.sort(qs,2);
                    S.Q=qs.first().getQuiver(this);
                    //System.out.println("Finished sorting the quivers.");
                    //System.out.println("Quiver taumutseq: "+S.Q.taumutseq);
                    S.Q.Hist.updatebuttons();
                }
            }
            repaint();
    }
    else if (actionCommand.equals("Next random w-quiver")){
            int i;
            int lastnonfrozen;
            do {
                lastnonfrozen=-1;
                for (i=0; i<S.Q.nbpoints; i++){
                    if (!S.Q.P[i].frozen){
                        lastnonfrozen=i;
                    }
                }
                if (lastnonfrozen>-1) {S.deletepoint(lastnonfrozen);}
            } while (lastnonfrozen>-1);

            for (i=0; i<S.Q.nbpoints; i++){
                S.Q.P[i].frozen=false;
            }
            S.Q.SeqDia.setVisible(false);
            S.Q.SeqDia=null;

            Permutation P=Permutation.rand(S.Q.nbpoints+1);
            //JOptionPane.showMessageDialog(this, "Permutation: "+ P+ "\nReduced word: "+P.redword());
            int[] sq=Utils.StringToIntArray(P.redword());
            S.removeAllMutables();
            S.Q.initializeWordquiver();
            double x,y;
            double tx=30;
            double ty=0;
            for (i=0; i<sq.length; i++){
                x=S.Q.P[sq[i]].x+(i+1)*tx;
                y=S.Q.P[sq[i]].y+(i+1)*ty;
                S.addwpoint((int) Math.round(x), (int) Math.round(y));
            }
            S.Q.finalizeWordquiver();


            SequencesDialog sd=new SequencesDialog(this, false);
            String taumutseq=S.Q.getWordquiverTaumutseq();
            String tauperm=S.Q.getWordquiverTauperm();
            sd.setSequence(taumutseq);
            sd.setPerm(tauperm);
            S.Q.setSeqDia(sd);
            int bound=100;

            int order=S.mutorder(taumutseq,tauperm,bound);
            String msg="";

            if (order>-1){
                    msg="Order="+order;
                    }
                else {
                   msg="Order > "+bound;
                }
            updatestatus(msg+"  ", MUTATING);
            //JOptionPane.showMessageDialog(this,"Order="+order);
            }
    else if (actionCommand.equals("Merge vertices")){
            S.removeAllMutables();
            updatePaneAndMenu();
            updatestatus(WAIT_FOR_MERGE_1);}
    else if (actionCommand.equals("Add arrows")){
            updatestatus(WAIT_FOR_NEW_SOURCE);}
    else if (actionCommand.equals("Delete nodes")){
            S.removeAllMutables();
            updatePaneAndMenu();
            updatestatus(DEL_NODES);}
    else if (actionCommand.equals("Freeze nodes")){
            updatestatus(FREEZE_NODES);}
    else if (actionCommand.equals("Show labels")){
            S.Q.setvertexradius(9);
            S.Q.showLabels=true; 
            showLabelsItem.setText("Hide labels") ;}
    else if (actionCommand.equals("Hide labels")){
            S.Q.setvertexradius(5);
            S.Q.showLabels=false; 
            showLabelsItem.setText("Show labels") ;}
    else if (actionCommand.equals("Hide frozen vertices")){
            S.Q.showFrozenVertices=false;
            showFrozenVerticesItem.setText("Show frozen vertices");
    }
    else if (actionCommand.equals("Show frozen vertices")){
            S.Q.showFrozenVertices=true;
            showFrozenVerticesItem.setText("Hide frozen vertices");
    }
    else if (actionCommand.equals("Switch traffic lights on")){
            S.Q.trafficLights=true;
            trafficLightsItem.setText("Switch traffic lights off");
    }
    else if (actionCommand.equals("Switch traffic lights off")){
            S.Q.trafficLights=false;
            S.Q.setDefaultColors();
            trafficLightsItem.setText("Switch traffic lights on");
    }
    else if (actionCommand.equals("Short numbers")){
            S.Q.shortNumbers=true;
            shortNumbersItem.setText("Long numbers") ;}
    else if (actionCommand.equals("Long numbers")){
            S.Q.shortNumbers=false;
            shortNumbersItem.setText("Short numbers") ;}
    else if (actionCommand.equals("Lambda quiver")){
            BMatrix B;
            if (S.Q.getLambda()!=null){
               B=S.Q.getLambda();
            }
            else {
                B=new BMatrix(S.Q.nbpoints, S.Q.nbpoints);
                B.makeZero();
                S.Q.setLambda(B);
            }
            BMatrix temp=S.Q.M;
            S.Q.M=B;
            LamQuiverDrawing=temp;
            lambdaQuiverItem.setText("Main quiver");
    }
    else if (actionCommand.equals("Main quiver")){
            BMatrix temp=S.Q.M;
            S.Q.M=LamQuiverDrawing;
            LamQuiverDrawing=temp;
            S.Q.setLambda(LamQuiverDrawing);
            /*
                    int[] fv=S.Q.getFrozenVertices();
            BMatrix SQTL=S.Q.BtransposeLambda().removeRows(fv);
            String s="B-transpose times Lambda equals \n\n"+SQTL+"\n";
            JOptionPane.showMessageDialog(this, s);
            */
            lambdaQuiverItem.setText("Lambda quiver");
        }
    else if (actionCommand.equals("Compute Lambda ...")){
         BMatrix B=null; BMatrix Lambda=null;
         if (lambdaQuiverItem.getText().equals("Main quiver")){
             B=new BMatrix(LamQuiverDrawing);
         }
         else {
             B=new BMatrix(S.Q.M);
         }
         Lambda=S.Q.initializeLambda(B);
         if (Lambda==null){
            String s="Cannot compute a compatible Lambda matrix. \n";
            if (lambdaQuiverItem.getText().equals("Main quiver")){
                s=s+"Enter a Lambda matrix by modifying this quiver.";
            }
            else {
                s=s+"Please enter a  Lambda matrix by choosing \"Lambda quiver\" in the \"View\" menu.";
            }
            JOptionPane.showMessageDialog(this, s);
         }
         else {
            if (lambdaQuiverItem.getText().equals("Main quiver")){
                S.Q.M=Lambda;
                JOptionPane.showMessageDialog(this,"Computed Lambda\n\n"+Lambda);
            }
            else {
                S.Q.setLambda(Lambda);
                JOptionPane.showMessageDialog(this,"Computed Lambda\n\n"+Lambda);
            }
         }
    }
    else if (actionCommand.equals("B^t * Lambda ...")){
        if (S.Q.getLambda()==null){
            JOptionPane.showMessageDialog(this, "Lambda is not yet defined. Choose \"Lambda quiver\" or \"Compute Lambda\".");
        }
        else {
            BMatrix Lambda; BMatrix B;
            if (lambdaQuiverItem.getText().equals("Main quiver")){
                Lambda=new BMatrix(S.Q.M);
                B=new BMatrix(LamQuiverDrawing);
            }
            else {
                Lambda=new BMatrix(S.Q.Lambda);
                B=new BMatrix(S.Q.M);
            }
            BMatrix BTL=B;
            BTL.transpose();
            BTL.multiplyby(Lambda);
            int[] fv=S.Q.getFrozenVertices();
            BTL=BTL.removeRows(fv);
            String s="B-transpose times Lambda equals \n\n"+BTL+"\n";
            JOptionPane.showMessageDialog(this, s);
        }
    }
    else if (actionCommand.equals("Show grid")){
            hasGrid=true;
            gridItem.setText("Hide grid") ;}
    else if (actionCommand.equals("Hide grid")){
            hasGrid=false;
            gridItem.setText("Show grid") ;}
    else if (actionCommand.equals("Show heights")){
            heightsBeingShown=true;
            S.Q.showHeights();
            heightItem.setText("Hide heights") ;}
    else if (actionCommand.equals("Hide heights")){
            heightsBeingShown=false;
            S.Q.hideHeights();
            heightItem.setText("Show heights") ;}
    else if (actionCommand.equals("Fit to grid")){
            S.Q.fitToGrid(gridsize);}
    else if (actionCommand.equals("Set grid size ...")){
            String s=JOptionPane.showInputDialog("Enter grid size (1-100",""+gridsize);
            if (s!=null){
            int size=Integer.parseInt(s);
            if ((0<size)&(size<101)){gridsize=size;}
            }}
     else if (actionCommand.equals("Scale and center")){
	    S.Q.scalecenter(getBounds());}
     else if (actionCommand.equals("Move left")){
        S.Q.moveLeft();}
     else if (actionCommand.equals("Add valued arrows")){
            updatestatus(WAIT_FOR_NEW_VSOURCE);}
     else if (actionCommand.equals("Antisymmetrizer ...")){
         BMatrix D=Utils.antisymmetrizingDiag(S.Q.M);
         String s="";
         for (int i=0;i<D.nbcols;i++){
             s=s+D.A[i][i];
             if (i<D.nbcols-1){
                 s=s+",";
             }
         }
         JOptionPane.showMessageDialog(this, s,"Antisymmetrizer",JOptionPane.INFORMATION_MESSAGE);
     }
     else if (actionCommand.equals("Matrix ...")){
         JOptionPane.showMessageDialog(this,S.Q.M.toString(),"Matrix",JOptionPane.INFORMATION_MESSAGE);
     }
     else if (actionCommand.equals("invisible")){
            chosenArrowStyle=1;
            updatestatus(WAIT_FOR_STYLE_SOURCE);}
     else if (actionCommand.equals("dashed")){
            chosenArrowStyle=2;
            updatestatus(WAIT_FOR_STYLE_SOURCE);}
     else if (actionCommand.equals("dotted")){
            chosenArrowStyle=3;
            updatestatus(WAIT_FOR_STYLE_SOURCE);}
     else if (actionCommand.equals("standard")){
            chosenArrowStyle=0;
            updatestatus(WAIT_FOR_STYLE_SOURCE);}
     else if (actionCommand.equals("all standard")){
            S.Q.StyleMatrix=null;
            updatestatus(MUTATING);}
     else if (actionCommand.equals("Oppose")){
        S.Q.oppose();
     }
    else if (actionCommand.equals("Invert")){
        Boolean ans=S.Q.invert();
        if (ans==null){
            JOptionPane.showMessageDialog(this, "The matrix associated with this quiver is not invertible:\n\n"+S.Q.M);
        }
     }
     
    else if (actionCommand.equals("Renumber nodes")){
            S.Q.setvertexradius(9);
            S.Q.showLabels=true; 
            S.Q.showFrozenVertices=true;
            showLabelsItem.setText("Hide labels") ;
            showFrozenVerticesItem.setText("Hide frozen vertices");
            nodenber=0;
            nodeseq=null;
            nodeseq=new int[S.Q.nbpoints];
            updatestatus(WAIT_FOR_NEXT_NODE);}
     else if (actionCommand.equals("Show history ...")){
	//JOptionPane.showMessageDialog(this, "History:\n"+Hist.toString());
        S.Q.Hist.show(fr);
     }
     else if (actionCommand.equals("Clear history")){
        S.Q.Hist.reset();}
     else if (actionCommand.equals("Set tau-data")){
         if (S.Q.getSeqDia()!=null){
             S.Q.taumutseq=S.Q.getSeqDia().sequenceField[0].getText();
             S.Q.tauperm=S.Q.getSeqDia().permField[0].getText();
         }
     }
     else if (actionCommand.equals("Sequence ...")){
        int[] seq=null;  
        String str=(String)JOptionPane.showInputDialog("Enter vertex sequence separated by spaces:", "");
        if (str!=null){
                   String[] fields=str.split(" ");
                   seq=new int[fields.length];
                   for (int i=0; i<fields.length; i++){
                     seq[i]=Integer.parseInt(fields[i])-1;
                   }
                if (seq!=null){
                       for (int i=0; i<seq.length; i++){
                           S.mutate(seq[i]);
                       }
                }
        }
     }
     else if (actionCommand.equals("Back")){
        if (status==DEL_NODES){
            S.Q.restoreQuiver();
            updatestatus(MUTATING);
        }
        else {
            int mutvert=S.Q.Hist.back();
            if (mutvert!=History.END_OF_HISTORY){
                if (mutsound){mutClip.play();}
                S.mutate(mutvert);}
        }
     }
     else if (actionCommand.equals("Forward")){
        int mutvert=S.Q.Hist.forward();
        if (mutvert!=History.END_OF_HISTORY){
            if (mutsound){mutClip.play();}
            S.mutate(mutvert);
        }
     }
     else if (actionCommand.equals("Open ...")){
        switch (applType){
            case APPLICATION: Open(); break;
            case WSAPPLICATION: WSOpen(); break;
        }
     }
    else if (actionCommand.equals("Add ...")){
        Add();
    }
    else if (actionCommand.equals("Minus ...")){
        Minus();
        updatestatus(status);
        //JOptionPane.showMessageDialog(this,"Test"+S.Q.taumutseq+"/n"+S.Q.tauperm);
        if (qs!=null) {
            //qs.reduce(AbstractQuiver.QUIVER_MOD_ICE);
        }
        updatestatus(status);
    }
    else if (actionCommand.equals("Unite ...")){
        Unite();
        updatestatus(status);
        //JOptionPane.showMessageDialog(this,"Test"+S.Q.taumutseq+"/n"+S.Q.tauperm);
        if (qs!=null) {
            qs.reduce(AbstractQuiver.QUIVER_MOD_ICE);
        }
        updatestatus(status);
    }
     else if (actionCommand.equals("Choose mut. sound ...")){
        if (applType==APPLICATION){
            AudioClip newmutClip=OpenSound();
            if (newmutClip!=null){
                mutClip.stop();
                mutClip=null;
                mutClip=newmutClip;
            }
        }
     }
    
    else if (actionCommand.equals("Choose bkgr. music ...")){
        if (applType==APPLICATION){
            AudioClip newbkgrClip=OpenSound();
            if (newbkgrClip!=null){
                bkgrClip.stop();
                bkgrClip=null;
                bkgrClip=newbkgrClip;
                if (bkgrsound){bkgrClip.loop();}
            }
        }
     }
    
    else if (actionCommand.equals("Mut. sound")){
        mutsound=!mutsound;
        MutSoundItem.setSelected(mutsound);
     }
    
    else if (actionCommand.equals("Bkgr. music")){
        bkgrsound=!bkgrsound;
        BkgrSoundItem.setSelected(bkgrsound);
        if (bkgrsound){
            bkgrClip.loop();
        }
        else {
            bkgrClip.stop();
        }
     }
     
     else if (actionCommand.equals("Save as ...")){
        SaveAs();
     }
    
     else if (actionCommand.equals("Save")){
        Save();
     }
            
     else if (actionCommand.equals("Save ...")){
        WSSave();
     }
        
     else if (actionCommand.equals("Print ...")){
        switch (applType){
            case APPLICATION: Print(); break;
            case WSAPPLICATION: WSPrint(); break;
        }
     }
    else if (actionCommand.equals("X-variables")){
        Xvariable xv=new Xvariable(S.Q.nbpoints);
        S.addMutable(xv);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    else if (actionCommand.equals("X-variables ...")){
        String s="";
        s=JOptionPane.showInputDialog(this, "X-variables will be computed starting from given" +
                " initial values.\nThese lie in the field generated over the rationals by given " +
                "indeterminates. \nPlease enter the indeterminates you wish to use, separated by commas",s);
        if (s!=null){
            //s=Utils.replaceAll(s," ",",");
            Ring R=new QuotientField(new PolynomialRing(Ring.Z, s));
            System.out.print("Ring R: "+R);
            int n=S.Q.nbpoints;
            s=JOptionPane.showInputDialog(this, "Enter the list of "+n+" initial X-variables, separated by commas");
            if (s!=null){
                String patternstr=",";
                String[] fields;
                fields=s.split(patternstr);
                if (fields.length!=n){
                    JOptionPane.showMessageDialog(this, "You entered "+fields.length+" initial variables instead of "+n+".");
                }
                else {
                    RingElt[] Xini=new RingElt[n];
                    for (int i=0; i<fields.length; i++){
                        Xini[i]=R.map(fields[i]);
                    }
                    Xvariable xv=new Xvariable(Xini);
                    xv.setIsMutable(true);
                    S.addMutable(xv);
                    //S.activateVariables();
                    updatePaneAndMenu();
                }
            }
        }
    }
    else if (actionCommand.equals("QX-variables")){
        if (S.Q.getLambda()==null){
            S.Q.initializeLambda(S.Q.M);
            if (S.Q.getLambda()==null){
                JOptionPane.showMessageDialog(this, "Cannot compute a compatible Lambda matrix. \n" +
                        "Please enter a a compatible Lambda matrix by choosing \"Lambda quiver\" in the \"View\" menu.");
            }
            else {
                int[] fv=S.Q.getFrozenVertices();
                BMatrix T=S.Q.BtransposeLambda().removeRows(fv);
                JOptionPane.showMessageDialog(this, "B transpose times Lambda equals \n\n"+T);
                QXvariable xv=new QXvariable(S.Q.nbpoints,S.Q.getLambda());
                S.addMutable(xv);
                updatePaneAndMenu();
                myQxv=xv;
            }
        }
        else {
            QXvariable xv=new QXvariable(S.Q.nbpoints,S.Q.getLambda());
            S.addMutable(xv);
            updatePaneAndMenu();
            myQxv=xv;
        }
    }
    
    else if (actionCommand.equals("Num. X-variables")){
        String s="1";
        for (int i=1;i<S.Q.nbpoints;i++){
            s=s+" 1";
        }
        s=JOptionPane.showInputDialog(this, "Enter initial values",s);
        if (s!=null){
            NumXvariable xv=new NumXvariable(s);
            S.addMutable(xv);
            //S.activateVariables();
            updatePaneAndMenu();
        }
    }
    
    else if (actionCommand.equals("Num. Xt-variables")){
        String s="1";
        for (int i=1;i<S.Q.nbpoints;i++){
            s=s+" 1";
        }
        s=JOptionPane.showInputDialog(this, "Enter initial values",s);
        if (s!=null){
            NumXtvariable xv=new NumXtvariable(s);
            S.addMutable(xv);
            //S.activateVariables();
            updatePaneAndMenu();
        }
    }
    
    else if (actionCommand.equals("XY-variables")){
        XYvariable xy=new XYvariable(S.Q.nbpoints);
        S.addMutable(xy);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("KS-variables")){
        XYBvariable xyb=new XYBvariable(S.Q.nbpoints);
        S.addMutable(xyb);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("QXF-variables")){
        QXFvariable qxf=new QXFvariable(S.Q.nbpoints);
        S.addMutable(qxf);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("Y-variables")){
        Yvariable yv=new Yvariable(S.Q.nbpoints);
        S.addMutable(yv);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("Y-variables ...")){
        String s="";
        s=JOptionPane.showInputDialog(this, "Y-variables will be computed starting from given" +
                " initial values.\nThese lie in the field generated over the rationals by given " +
                "indeterminates. \nPlease enter the indeterminates you wish to use, separated by commas",s);
        if (s!=null){
            //s=Utils.replaceAll(s," ",",");
            Ring R=new QuotientField(new PolynomialRing(Ring.Z, s));
            System.out.println("Ring R: "+R);
            int n=S.Q.nbpoints;
            s=JOptionPane.showInputDialog(this, "Enter the list of the "+n+" initial Y-variables, separated by commas");
            if (s!=null){
                String patternstr=",";
                String[] fields;
                fields=s.split(patternstr);
                if (fields.length!=n){
                    JOptionPane.showMessageDialog(this, "You entered "+fields.length+" initial variables instead of "+n+".");
                }
                else {
                    RingElt[] Yini=new RingElt[n];
                    for (int i=0; i<fields.length; i++){
                        Yini[i]=R.map(fields[i]);
                    }
                    Yvariable yv=new Yvariable(Yini);
                    yv.setIsMutable(true);
                    S.addMutable(yv);
                    //S.activateVariables();
                    updatePaneAndMenu();
                }
            }
        }
    }
    
    else if (actionCommand.equals("Yt-variables")){
        Ytropvariable yt=new Ytropvariable(S.Q.nbpoints);
        S.addMutable(yt);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("F-polynomials")){
        Fpolynomial fp=new Fpolynomial(S.Q.nbpoints);
        S.addMutable(fp);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("F'-polynomials")){
        Fppolynomial fp=new Fppolynomial(S.Q.nbpoints);
        S.addMutable(fp);
	updatePaneAndMenu();
    }
    
    
    else if (actionCommand.equals("d-vectors")){
        Dvector dv=new Dvector(S.Q.nbpoints);
        S.addMutable(dv);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("f-vectors")){
        Fvector fv=new Fvector(S.Q.nbpoints);
        S.addMutable(fv);
        //S.activateVariables();
	updatePaneAndMenu();
    }
    
    
    else if (actionCommand.equals("Set Cartan matrix")){
        S.initializeCartanMatrix();
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("Guo-vectors")){
        String s=JOptionPane.showInputDialog("Enter the "+(S.Q.nbpoints)+" components of the vector y separated by spaces");
        if (s!=null){
            String patternstr=" ";
            String[] fields=s.split(patternstr);
            BigInteger[] y=new BigInteger[S.Q.nbpoints];
            for (int i=0; i<S.Q.nbpoints; i++){
                y[i]=new BigInteger(fields[i]);
            }
        Guovector gv=new Guovector(S.Q.nbpoints, S.Q.M, y);
        S.addMutable(gv);
	updatePaneAndMenu();
        }
    }
    
    else if (actionCommand.equals("g-vectors")){
        Gvector gv=new Gvector(S.Q.nbpoints, S.Q.M);
        S.addMutable(gv);
	updatePaneAndMenu();
    }
    
    
    
    else if (actionCommand.equals("g-lincombs")){
        Gvector gv=new Glincomb(S.Q.nbpoints, S.Q.M);
        S.addMutable(gv);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("g-matrix")){
        Gmatrix gv=new Gmatrix(S.Q.nbpoints, S.Q.M);
        S.addMutable(gv);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("gr-vectors")){
        GRvector gr=new GRvector(S.Q.nbpoints, S.Q.M);
        S.Q.setColor(Color.GREEN);
        S.addMutable(gr);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("h-vectors")){
        Hvector hv=new Hvector(S.Q.nbpoints, S.Q.M);
        S.addMutable(hv);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("g-Tracker")){
        Gvector gv=new Gvector(S.Q.nbpoints,S.Q.M);
        myTracker=new Tracker(S.Q.nbpoints,gv, S.Q.M);
        S.addMutable(myTracker);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("h-Tracker")){
        Hvector hv=new Hvector(S.Q.nbpoints, S.Q.M);
        myTracker=new Tracker(S.Q.nbpoints,hv, S.Q.M);
        S.addMutable(myTracker);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("d-Tracker")){
        Dvector dv=new Dvector(S.Q.nbpoints);
        myTracker=new Tracker(S.Q.nbpoints,dv,S.Q.M);
        S.addMutable(myTracker);
	updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("jvx output ...")){
            TextDisplayDialog.showDialog(fr,fr,"jvx", myTracker.tojvx(),5,40);
    }
    
    else if (actionCommand.equals("Traverse ...")){
        String s=JOptionPane.showInputDialog(this, "Enter n");
        int n=Integer.parseInt(s);
        s="";
        Quiver q=new Quiver(S.Q.M,this);
        myTracker.traverseTree(n,-1,s,q);
    } 
        
    else if (actionCommand.equals("Reset vars./dim.")){
        S.resetCluster();
        updatePaneAndMenu();
    }

    else if (actionCommand.equals("Font size ...")){
        String s=JOptionPane.showInputDialog("Enter font size (6-200)",""+S.dPane.getFontSize());
        if (s!=null){
        int size=Integer.parseInt(s);
        if ((5<size)&(size<200)){S.dPane.setFontSize(size);}
        }
    }
    
    else if (actionCommand.equals("Show numbers")){
        S.C.setShowNumbers(true);
        updatePaneAndMenu();
    }

    else if (actionCommand.equals("Show last X-mut. ...")){
        Xvariable locXv=new Xvariable(S.Q.nbpoints);
        if (S.Q.Hist.present()==-1){
            JOptionPane.showMessageDialog(this,"No mutation performed yet.");
        }
        else{
            int lastmut=S.Q.Hist.present();
            locXv.mutate(S.Q, lastmut);
            TextDisplayDialog.showDialog(fr,fr,"Last X-mutation", locXv.toString(lastmut),5,40);
        }
    }
    
    else if (actionCommand.equals("Hide numbers")){
        S.C.setShowNumbers(false);
        updatePaneAndMenu();
    }
    
    else if (actionCommand.equals("Random ...")){
        String s=JOptionPane.showInputDialog("Enter number of random mutations (0-1000)",""+randnber);
        if (s!=null){
        int nber=Integer.parseInt(s);
        if ((-1<nber)&(nber<1001)){
            randnber=nber;
           for (int i=0; i<nber; i++){
               Vector RedVertices=new Vector(10,1);
               for (int j=0; j<S.Q.nbpoints; j++){
                   if ((S.Q.P[j].mycolor.equals(Color.RED))&& (!(S.Q.P[j].frozen))){
                       RedVertices.add(new Integer(j));
                       //System.out.println("Red vertex: " + j);
                   }
               }
               int j=MyRandom.nextInt(RedVertices.size());
               int r=((Integer) RedVertices.elementAt(j)).intValue();
               S.mutate(r);
               /*//System.out.println("Trying to mutate "+ (r+1));
               if ((S.Q.P[r].mycolor.equals(Color.RED))&& (!(S.Q.P[r].frozen))){
                   //System.out.println((r+1)+" is red.");
                    S.mutate(r);
               }
               else {
                   //System.out.println((r+1)+ " is not red.");
               }
                */
               S.Q.Hist.add(r);
           } 
        }
    }}
    else if (actionCommand.equals("Repeat random")){
        for (int i=0; i<randnber; i++){
               Vector RedVertices=new Vector(10,1);
               for (int j=0; j<S.Q.nbpoints; j++){
                   if ((S.Q.P[j].mycolor.equals(Color.RED))&& (!(S.Q.P[j].frozen))){
                       RedVertices.add(new Integer(j));
                       //System.out.println("Red vertex: " + j);
                   }
               }
               int j=MyRandom.nextInt(RedVertices.size());
               int r=((Integer) RedVertices.elementAt(j)).intValue();
               S.mutate(r);
               /*//System.out.println("Trying to mutate "+ (r+1));
               if ((S.Q.P[r].mycolor.equals(Color.RED))&& (!(S.Q.P[r].frozen))){
                   //System.out.println((r+1)+" is red.");
                    S.mutate(r);
               }
               else {
                   //System.out.println((r+1)+ " is not red.");
               }
                */
               S.Q.Hist.add(r);
    }
    }
    else if (actionCommand.equals("Store/recall quiver")){
        S.swapQmem(); 
    }
    else if (actionCommand.equals("Show weight ...")){
        JOptionPane.showMessageDialog(this, "This valued quiver is of weight "+S.weight()+".");
    }
    else if (actionCommand.equals("Make lighter ...")){
    String s=JOptionPane.showInputDialog(this,"By random mutations, we try to decrease the weight.\n" 
     + "Enter number of random mutations to perform (1-10000)", ""+randnber);
        if (s!=null){
            int nber=Integer.parseInt(s);
            if ((0<nber)&(nber<10001)){
                makeLighter(nber);
            }
        }
    }
    else if (actionCommand.equals("Repeat make lighter")){
        makeLighter(randnber);
    }
    else if (actionCommand.equals("Look for ...")){
     String s=JOptionPane.showInputDialog(this,"Looking for double arrows.\n" 
     + "Enter maximal number of random mutations to perform (1-10000)", "500");
        if (s!=null){
            int nber=Integer.parseInt(s);
            if ((0<nber)&(nber<10001)){
                int i=0;
                boolean found=S.Q.hasDoubleArrow();
                while ((i<=nber)&(!found)){
                    int r=MyRandom.nextInt(S.Q.nbpoints);
                    S.mutate(r);
                    S.Q.Hist.add(r);
                    found=S.Q.hasDoubleArrow();
                    i++;
                }
                if (found){
                    JOptionPane.showMessageDialog(this,"Found a double arrow in "+(i-1)+" steps.");
                }
                else {
                    JOptionPane.showMessageDialog(this,"Did not find a double arrow in "+(i-1)+" steps.");
                }
            }
          
        }
    }
    else if (actionCommand.equals("Color vertices ...")){
        newNodeColor = JColorChooser.showDialog(
                     (JPanel) this,
                     "Choose vertex color", null);
        if (newNodeColor!=null){
            updatestatus(COLOR_NODES);
        }
    }
    else if (actionCommand.equals("Color all vertices ...")){
        newNodeColor = JColorChooser.showDialog(
                     (JPanel) this,
                     "Choose vertex color", null);
        if (newNodeColor!=null){
            S.Q.setColor(newNodeColor);
        }
    }
    else if (actionCommand.equals("Set radius ...")){
	String s=(String)JOptionPane.showInputDialog("Present radius: " + 
                S.Q.getvertexradius()+ "\n Enter radius (1-100): ");
        if (s!=null){
            int i=Integer.parseInt(s);
	    if ((0<i) && (i<101)){
		    S.Q.setvertexradius(i);
                    updatestatus(status);
	    }
	}
    }
    else if (actionCommand.equals("Arrow labels ...")){
	String s=(String)JOptionPane.showInputDialog("Present arrow label size: " + 
                S.Q.getarrowlabelsize()+ "\n Enter new label size (1-100): ");
        if (s!=null){
            int i=Integer.parseInt(s);
	    if ((0<i) && (i<101)){
		    S.Q.setarrowlabelsize((float) i);
                    updatestatus(status);
	    }
	}
    }
    else if (actionCommand.equals("Set boundary ...")){
	String s=(String)JOptionPane.showInputDialog("Present boundary factor: " + 
                S.Q.getgrowthFactor()+ "\n Enter factor (0.0-1.0): ");
        if (s!=null){
            float f=Float.parseFloat(s);
	    if ((0<=f) && (f<=1)){
		    S.Q.setgrowthFactor(f);
                    S.Q.scalecenter(getBounds());
	    }
	}
    }
    else if (actionCommand.equals("Export to xypic ...")){
        TextDisplayDialog.showDialog(fr,fr,"LaTeX xypic", S.Q.toxy(),5, 40);
    }
    else if (actionCommand.equals("Dimensions ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                meshlength=S.Q.nbpoints-1;
                BMatrix A=S.Q.repeat(nber);
                //System.out.println("Main:"+A);
                if (A!=null){
                    Dispvector dv=new Dispvector(A);
                    dv.setType("dim");
                    S.addMutable(dv);
                    updatePaneAndMenu();
                    S.Q.dimVect=A;
                    int ans=JOptionPane.showConfirmDialog(this, "Would you like to enter tilting summands?");
                    if (ans==JOptionPane.OK_OPTION){
                        System.out.println("User chose OK option : "+ans);
                        tiltingSummand=new boolean[S.Q.nbpoints];
                        for (int i=0; i<S.Q.nbpoints;i++){tiltingSummand[i]=false;}
                        status=WAIT_FOR_TILTING_SUMMAND;
                    }
                    else {
                        System.out.println("User did not choose OK option: "+ans);
                        status=MUTATING;
        
                        ans=JOptionPane.showConfirmDialog(this, "Would you like to enter exceptional summands?");
                        if (ans==JOptionPane.OK_OPTION){
                            System.out.println("User chose OK option : "+ans);
                            tiltingSummand=new boolean[S.Q.nbpoints];
                            for (int i=0; i<S.Q.nbpoints;i++){tiltingSummand[i]=false;}
                            status=WAIT_FOR_EXC_SUMMAND;
                        }
                        else {
                            System.out.println("User did not choose OK option: "+ans);
                            status=MUTATING;
                            
                            ans=JOptionPane.showConfirmDialog(this, "Would you like to mark the simples?");
                            if (ans==JOptionPane.OK_OPTION){
                                System.out.println("User chose OK option : "+ans);
                                for (int i=0; i<S.Q.nbpoints;i++){
                                    if (S.Q.dimVect.rowIsSimple(i)){
                                        S.Q.P[i].setColor(Color.YELLOW);
                                    }
                                }
                                
                                status=MUTATING;
                            }
                            else {
                                System.out.println("User did not choose OK option: "+ans);
                                status=MUTATING;
                            }
                        }
                        
                
                    }   
                }
            }
        }
    }
    else if (actionCommand.equals("Category S ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                meshlength=S.Q.nbpoints-1;
                BMatrix A=S.Q.repeat(nber);
                //System.out.println("Main:"+A);
                if (A!=null){
                    S.Q.dimVect=A;
                    BMatrix B=new BMatrix(S.Q.nbpoints,S.Q.nbpoints);
                    B.makeZero();
                    for (int i=0; i<S.Q.nbpoints; i++){
                        for (int j=0; j<S.Q.nbpoints; j++){
                            B.A[i][j]=Utils.max(dimHomTauminus(i,j), BigInteger.ZERO);
                        }
                    }
                    B.antisymmetrize();
                    //System.out.println("Matrix for I:");
                    //System.out.println(B.toString());
                    S.Q.setColor(Color.BLUE);
                    S.Q.M.copyfrom(B);
                    S.Q.scalecenter(getBounds());
                    //updatePaneAndMenu();
                    
                }
            }
        }
    }
    else if (actionCommand.equals("Clusters ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)",""+friezenber);
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                friezenber=nber;
                RingElt[] X=S.Q.repeatwithcluster(nber, false, "");
                if (X!=null){
                    Xvariable xv=new Xvariable(X);
                    S.addMutable(xv);
                    updatePaneAndMenu();
                }
            }
        }
    }
    else if (actionCommand.equals("Frieze ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)", ""+friezenber);
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                friezenber=nber;
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(2*S.Q.nbpoints)+1);
                }
                s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                    S.setMemQuiver(S.Q.copy());
                    RingElt[] X=S.Q.repeatwithcluster(nber, true,s);
                    if (X!=null){
                        Xvariable xv=new Xvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                    }
                }
            }
        }
    }
    
    else if (actionCommand.equals("Repeat frieze ...")){
        
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)",""+friezenber);
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                S.removeAllMutables();
                S.Q=S.getMemQuiver().copy();
                updatePaneAndMenu();
                friezenber=nber;
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(2*S.Q.nbpoints)+1);
                }
                s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                    RingElt[] X=S.Q.repeatwithcluster(nber, true,s);
                    if (X!=null){
                        Xvariable xv=new Xvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                    }
                }
            }
        }
    }
    
    else if (actionCommand.equals("Trop. frieze ...")){
       
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)",""+friezenber);
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                friezenber=nber;
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(2*S.Q.nbpoints)-S.Q.nbpoints);
                }
                s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                     S.Q.setvertexradius(11);
                     updatestatus(status);
                     S.Q.setgrowthFactor((float) 0.05);
                     S.Q.scalecenter(getBounds());
                     S.setMemQuiver(S.Q.copy());
                    BigInteger[] X=S.Q.repeatwithtropcluster(nber, s);
                    if (X!=null){
                        NumXtvariable xv=new NumXtvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                        S.Q.setvertexradius(11);
                        updatestatus(status);
                    }
                }
            }
        }
    }
    
    else if (actionCommand.equals("Repeat trop. frieze ...")){
        
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)",""+friezenber);
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                S.removeAllMutables();
                S.Q=S.getMemQuiver().copy();
                updatePaneAndMenu();
                friezenber=nber;
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(2*S.Q.nbpoints)-S.Q.nbpoints);
                }
                s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                    
                    BigInteger[] X=S.Q.repeatwithtropcluster(nber, s);
                    if (X!=null){
                        NumXtvariable xv=new NumXtvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                        S.Q.setvertexradius(11);
                        updatestatus(status);
                    }
                }
            }
        }
    }

    
    
    else if (actionCommand.equals("Repeat trop. frieze")){
 
        String s=""+friezenber;
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                S.removeAllMutables();
                S.Q=S.getMemQuiver().copy();
                updatePaneAndMenu();
                friezenber=nber;
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(4*S.Q.nbpoints)-2*S.Q.nbpoints);
                }
                //s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                    
                    BigInteger[] X=S.Q.repeatwithtropcluster(nber, s);
                    if (X!=null){
                        NumXtvariable xv=new NumXtvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                        S.Q.setvertexradius(11);
                        updatestatus(status);
                        S.Q.scalecenter(getBounds());
                    }
                }
            }
        }
    }

    else if (actionCommand.equals("Cluster add. fct. ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                s="";
                for (int i=0; i<S.Q.nbpoints-1;i++){
                    if (i>0){s=s+" ";}
                    s=s+(MyRandom.nextInt(2*S.Q.nbpoints)-S.Q.nbpoints);
                }
                s=JOptionPane.showInputDialog(this, "Enter initial values separated by spaces",s);
                if (s!=null){
                    BigInteger[] X=S.Q.repeatwithclusteradd(nber, s);
                    if (X!=null){
                        NumXtvariable xv=new NumXtvariable(X);
                        S.addMutable(xv);
                        updatePaneAndMenu();
                    }
                }
            }
        }
    }
    else if (actionCommand.equals("AR-arrows (Delta) ...")){
    String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            BigInteger[] h=new BigInteger[nber*S.Q.nbpoints];
            if (nber>0 & nber<101){
                BMatrix A=S.Q.repeatwithDeltaleft(nber,h);
                //System.out.println("Main:"+A);
                if (A!=null){
                    Grepvector gr=new Grepvector(A,h);
                    S.addMutable(gr);
                    updatePaneAndMenu();
                }
            }
        }
    }
    else if (actionCommand.equals("AR-arrows (Delta) ...")){
    String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            BigInteger[] h=new BigInteger[nber*S.Q.nbpoints];
            if (nber>0 & nber<101){
                BMatrix A=S.Q.repeatwithDeltaleft(nber,h);
                //System.out.println("Main:"+A);
                if (A!=null){
                    Grepvector gr=new Grepvector(A,h);
                    S.addMutable(gr);
                    updatePaneAndMenu();
                }
            }
        }
    }
    else if (actionCommand.equals("AR-arrows (preinj.) ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                BMatrix A=S.Q.repeatwithdimleft(nber);
                //System.out.println("Main:"+A);
                if (A!=null){
                    Grepvector gr=new Grepvector(A);
                    S.addMutable(gr);
                    updatePaneAndMenu();
                }
            }
        }
    }
    else if (actionCommand.equals("Highest weights ...")){
        String s=JOptionPane.showInputDialog(this,"The translation vector is from the 2nd last to the last node.\n" +
                "The last node will be deleted.\nEnter number of copies (1-100)","5");
        if (s!=null && s.length()>0){
            int nber=Integer.parseInt(s);
            if (nber>0 & nber<101){
                BMatrix C=new BMatrix(S.Q.nbpoints-1,S.Q.nbpoints-1);
                C.copyfrom(S.Q.M, S.Q.nbpoints-1);
                BMatrix A=S.Q.repeatwithweight(nber);
                //System.out.println("Main:"+A);
                if (A!=null){
                    Weightvector wt=new Weightvector(A,C);
                    S.addMutable(wt);
                    updatePaneAndMenu();
                }
            }
        }
    } 
   else if (actionCommand.equals("Export to jvx ...")){
        Object[] possibilities = {"Flat strip", "Cylinder", "Moebius strip"};
        String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Choose 3D form",
                    "Export to jvx",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "Flat strip");
        if ((s != null) && (s.length() > 0)) {
            int option=0;
            while (!s.equals((String) possibilities[option])){option++;}
            TextDisplayDialog.showDialog(fr,fr,"LaTeX xypic", S.Q.tojvx(option),5,40);
        }
    }
    else if (actionCommand.equals("Hints ...")){
        String s="Show heights \n" +
                "Each node has a z-coordinate called its height. The heights\n" +
                "are displayed by this command. To increase the height of a node, ctrl-click it.\n" +
                "To decrease its height, alt-ctrl-click it. The heights are used when generating\n" +
                "the 3D image in the javaview file. Cf. Tools/Export to jvx ...\n" +
                "\n" +
                "Dimensions ...\n" +
                "Given a quiver Q, this command generates a piece of the quiver ZQ called the\n" +
                "repetition of Q and obtained by adding translated copies of Q and joining them\n" +
                "by new arrows: for each arrow from x to y, a new arrow from y to the translate of x.\n" +
                "For the translation, the program uses the vector joining the second last node\n" +
                "to the last node. It always deletes the last node.\n\n" +
                "Export to xypic ...\n" +
                "This command displays a window containing the xypic representation of the\n" +
                "quiver. It can be copied and pasted into a text editor. Make the quiver\n" +
                "mutation window small to obtain a small xypic figure.\n" +
                "\n" +
                "Export to jvx ...\n" +
                "This command displays a window containing the jvx representation of the\n" +
                "quiver in one of three forms: flat, as a cyclinder or as a Moebius strip.\n" +
                "The jvx file is a text file which can be opened with the javaviewer, \n" +
                "cf. www.javaview.de. For output as a cyclinder or Moebius strip, the \n" +
                "program glues the right hand edge with the left hand edge of the quiver.\n" +
                "If these fit nicely, no cut will be visible after glueing.";
        TextDisplayDialog.showDialog(fr, fr, "Hints", s, 25,40);
	     }
    
    else if (actionCommand.equals("About ...")){
            Runtime runtime=Runtime.getRuntime();
            long maxMemory=runtime.maxMemory();
            long allocatedMemory=runtime.totalMemory();
            long freeMemory=runtime.freeMemory();
            
		 JOptionPane.showMessageDialog(this, 
					       "Quiver mutation\n" +
                 "September 9, 2016\n\nBernhard Keller\n" +
                 "University Paris Diderot - Paris 7\nJussieu Mathematics Institute\n\n"+
                         "Free memory: "+freeMemory/1024 +"\n"+
                         "Allocated memory: "+allocatedMemory/1024+"\n"+
                         "Maximal memory: "+maxMemory/1024+"\n"+
                         "Total free memory: "+(freeMemory+(maxMemory-allocatedMemory))/1024);
    }
    else if (actionCommand.equals("First quiver")){
        S.Q=qs.first().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
    else if (actionCommand.equals("Next quiver")){
        S.Q=qs.next().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
    else if (actionCommand.equals("Previous quiver")){
	S.Q=qs.previous().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
    else if (actionCommand.equals("Nb. mult. arr. ...")){
        if (qs!=null){
            JOptionPane.showMessageDialog(this, "Number of quivers with multiple arrows:"+qs.nbQuivWithMultArrows());
        }
    }
     else if (actionCommand.equals("Mult. arrows first")){
        qs.updateCurrentFromEmbeddedQuiver();
        QuiverSetSorter.sort(qs,1);
        S.Q=qs.first().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
    else if (actionCommand.equals("Long hist. first")){
        qs.updateCurrentFromEmbeddedQuiver();
        QuiverSetSorter.sort(qs,3);
        S.Q=qs.first().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
    else if (actionCommand.equals("Weight order")){
        qs.updateCurrentFromEmbeddedQuiver();
        QuiverSetSorter.sort(qs);
        S.Q=qs.first().getQuiver(this);
        S.Q.Hist.updatebuttons();
        updatestatus(status);
    }
     else if (actionCommand.equals("Clear")){
        qs=null;
        qs=new QuiverSet(this);
        updatetoolmenu();
        updatestatus(status);
    }
    else if (actionCommand.equals("Go to quiver ...")){
	String s=(String)JOptionPane.showInputDialog("Present quiver number: " + 
                qs.getcter()+ "\n Go to quiver number (1-" + (qs.getnber())+
						     ") :", ""+qs.getcter());
        if (s!=null){
            int i=Integer.parseInt(s);
	    if ((1<=i) && (i<=qs.getnber())){
		    S.Q=qs.elementAt(i-1).getQuiver(this);
                    qs.setcter(i-1);
                    S.Q.Hist.updatebuttons();
                    updatestatus(status);
	    }
	}
    }
    else if (actionCommand.equals("Mutation class ...")){

	TraverseDialog dia=new TraverseDialog(fr, this, qs, S);
        updatestatus(status);

	/*AbstractQuiver aq=new AbstractQuiver(S.Q.M);
	aq.setQuiver(S.Q);
	System.out.println("Size before: " + qs.getnber());
        qs.append(aq);
	System.out.println("Size after : " + qs.getnber());
	S.Q=qs.first().getQuiver(this);        
	System.out.println("Size after first: " + qs.getnber());
	*/
      }
    else if (actionCommand.equals("Store")){
        qs.v.add(new AbstractQuiver(S.Q.M, this));
    }
    else if (actionCommand.equals("Metric quiver ...")){
        String str;
        S.Q.Oxvector=null;
        BMatrix K=S.Q.M.Kernel();
        if (K==null){
            JOptionPane.showMessageDialog(this, "The Euler form is non singular. The metric quiver is not defined.");
        }
        else {
            //K.toJama().print(5,2);
            K.transpose();
            if (K.nbrows==1){
                S.Q.Oxvector=K.row(0);
            }
            if (S.Q.Oxvector==null){
                str=(String) JOptionPane.showInputDialog("Kernel: \n" + K.toString() +
                    "Enter components of dim(O_x) separated by spaces: ");
                if (str!=null){
                    S.Q.setOxvector(str);
                }
            }
            int i=0;
            int[] cycle=null;
            while ((cycle==null) && (i<S.Q.nbpoints)){
                cycle=S.Q.cycleThrough(i);
                i=i+1;
            }
            if (S.Q.Oxvector!=null){
                if ((cycle!=null) && (Utils.omnipresent(S.Q.Oxvector))){
                    boolean NonZeroChargeSum=S.Q.setRCharges(cycle);
                    if (NonZeroChargeSum){
                        S.Q.computeAngles();
                        S.Q.scalecenter(getBounds());
                    }
                }
            }
        }
    }
    else if (actionCommand.equals("Enter cycle ...")){
        String str;
        int[] cycle=null;
        str=(String)JOptionPane.showInputDialog("Enter the vertices of a potential cycle separated by spaces: ");
                if ((str!=null) && (str!="")){
                   String[] fields=str.split(" ");
                   cycle=new int[fields.length];
                   for (int i=0; i<fields.length; i++){
                   cycle[i]=Integer.parseInt(fields[i])-1;
                   }
                }
        if ((cycle!=null) && (Utils.omnipresent(S.Q.Oxvector))){
            S.Q.setRCharges(cycle);
            S.Q.computeAngles();
            S.Q.scalecenter(getBounds());
        }
    }
    else if (actionCommand.equals("Forget metric")){
        S.Q.Oxvector=null;
    }
    else if (actionCommand.equals("Central charge ...")){
        if (S.Q.dimVect!=null){
            S.Q.determineSimples(false); // no origin present yet
            //S.Q.addOrigin();
            System.out.println("Entering S.Q.determineSubmodules");
            S.Q.determineSubmodules();
            updatestatus(MODIFYING_CENTRAL_CHARGE);
        }
        else {
            JOptionPane.showMessageDialog(this, "You first need to compute the dimension vectors.");
        }
    }
    else if (actionCommand.equals("Enter dim. vector ...")){
        String str=JOptionPane.showInputDialog(this, "Enter the dimension vector with entries separated by spaces");
        if (str.length()>0){
               String[] fields=str.split(" ");
               BigInteger[] v=new BigInteger[fields.length];
               for (int j=0; j<fields.length; j++){
                   v[j]=new BigInteger(""+Integer.parseInt(fields[j]));
               }
               S.Q.dimVect.appendRow(v);
               System.out.println("1. dimVect:\n"+S.Q.dimVect);
               String str1=JOptionPane.showInputDialog(fr, "Enter the indec. submodules separated by spaces");
               if (str1.length()>0){
                   fields=str1.split(" ");
                   int[] p=new int[fields.length];
                   for (int j=0; j<fields.length; j++){
                       p[j]=Integer.parseInt(fields[j])-1;
                   }
                   S.Q.submodules.add(p);
               }
               else {
                   int[] p=new int[0];
                   S.Q.submodules.add(p);
               }
               S.Q.addnode(0,0);
               int n=S.Q.nbpoints;
               MoveablePoint Origin=S.Q.P[n-2];
               S.Q.P[n-2]=S.Q.P[n-1];
               S.Q.P[n-1]=Origin;
               System.out.println("Nb points: "+S.Q.nbpoints);
               System.out.println("2. dimVect:\n"+S.Q.dimVect);
           }
    }
    else if (actionCommand.equals("Enter submodules ...")){
        String str=JOptionPane.showInputDialog(this, "Enter the number of simples");
        if (str!=null){
            if (str.length()>0){
                int n=Integer.parseInt(str);
                BMatrix futuredimVect=new BMatrix(S.Q.nbpoints,n);
                futuredimVect.enterMatrix(fr, "Enter the dimension vectors");
                S.Q.enterSubmodules(fr);
                S.Q.dimVect=futuredimVect;
                S.Q.determineSimples(false); // no origin present yet
                //S.Q.addOrigin();
            }
        }
    }
    else if (actionCommand.equals("Toggle spikes")){
        S.Q.toggleSpikes();
    }
    else if (actionCommand.equals("Add frozen vertices")){
       S.Q.addExtendingVertices();
    }
    else if (actionCommand.equals("Add framing")){
        S.Q.addFraming();
        S.Q.trafficLights=true; 
        trafficLightsItem.setText("Switch traffic lights off");
    }
    else if (actionCommand.equals("Merge vertices")){
            updatestatus(WAIT_FOR_MERGE_1);
    }
    else if (actionCommand.equals("Test3 ...")){
        Quiver Q1=S.Q;
        Quiver Q2=S.getMemQuiver();
        int[] isom=AbstractQuiver.searchIsom(Q1,Q2,this);
        
        System.out.println("Conjugate Isomorphism: "+Utils.toString(isom));
        
        
    }
    else if (actionCommand.equals("Reduce graph mod frozen")){
        if (qs!=null) {
            qs.reduce(AbstractQuiver.QUIVER_MOD_ICE);
        }
        updatestatus(status);
    }
    else if (actionCommand.equals("Nb. of tau-finite quivers ...")){
        if (qs!=null) {
           JOptionPane.showMessageDialog(this,"Number of tau-finite quivers: "+qs.nbTaufiniteQuivers());
        }
    }
    else if (actionCommand.equals("tau")){
        
        int[] tms=Utils.StringToIntArray(S.Q.taumutseq);
        int[] tp=Utils.StringToIntArray(S.Q.tauperm);
        //JOptionPane.showMessageDialog(this,"Test\n"+tms+"\n"+tp);
        S.mutate(tms, tp);
    }
    else if (actionCommand.equals("Show tau-data ...")){
        JOptionPane.showMessageDialog(this,"Tau\nMut.: "+S.Q.taumutseq+"\nPerm.: "+S.Q.tauperm);
    }
    else if (actionCommand.equals("tau inverse")){
        
        int[] tms=Utils.StringToIntArray(S.Q.taumutseq);
        int[] tp=Utils.StringToIntArray(S.Q.tauperm);
        //JOptionPane.showMessageDialog(this,"Test\n"+tms+"\n"+tp);
        S.invmutate(tms, tp);
        
    }
    else if (actionCommand.equals("Test2 ...")){
        while ((qs.size()>1)&&(S.Q.isAcyclic())){
            S.Q=qs.next().getQuiver(this);
            S.Q.Hist.updatebuttons();  
            qs.removePreceding();
        }
        updatestatus(status);
        if ((qs.size()<=1)&&(S.Q.isAcyclic())){
            System.out.println("This quiver is acyclic.");
        }
        else {
        BMatrix M=S.Q.MatrixWithoutDashedArrows();
        int [] sinks=M.sinks();
        System.out.println("Sinks w/o dashed arrows: "+Utils.toString(sinks));
        if (sinks==null) {
               S.Q=qs.next().getQuiver(this);
               S.Q.Hist.updatebuttons();  
            }
        else {
            M=S.Q.MatrixOfDashedArrows();
            int[] sc=null;
            int v1=-1;
            for (int i=0; i<sinks.length; i++){
                sc=M.successorChain(sinks[i]);
                Quiver Q1;
                System.out.println("Successor chain of "+ sinks[i]+": "+Utils.toString(sc));
                if (sc!=null){
                    Q1=S.Q.copy();
                    for (int j=0; j<sc.length;j++){
                        Q1.mutate(sc[j],1);
                    }
                    if (Q1.isSink(sinks[i])){
                        v1=sinks[i]; System.out.println("v1="+v1+" is a pre-sink.");
                    }
                } else {
                    if ((S.Q.isSink(sinks[i]))&&(!S.Q.isSource(sinks[i]))){
                        v1=sinks[i]; 
                        System.out.println("v1="+v1+" is a sink.");
                        System.out.println("Predecessors of "+v1+" :"+Utils.toString(S.Q.immediatePredec(v1)));
                    }
                }
            }
            if (v1>=0){
                sc=M.successorChain(v1);
                //System.out.println("Successor chain: "+Utils.toString(sc));
                if (sc!=null){
                    for (int i=0; i<sc.length;i++){
                        S.mutate(sc[i]);
                    }
                }
                
                System.out.println("v1="+v1+" is sink (after mutations): "+S.Q.isSink(v1));
                     
                int v2;
                if (sc!=null){
                    v2=sc[sc.length-1];
                }
                else {
                    System.out.println("Predecessors of "+v1+" :"+Utils.toString(S.Q.immediatePredec(v1)));
                    v2=S.Q.immediatePredec(v1)[0];
                }
                
                Quiver Q1=S.Q.copy();
                System.out.println("Deleting "+v1);
                Q1.deletenode(v1);
                int tomutate;
                if (sc!=null){
                    for (int i=sc.length-1;i>=0;i--){
                        if (sc[i]<=v1){
                            tomutate=sc[i];
                        }
                        else {
                            tomutate=sc[i]-1;
                        }
                        //System.out.println("Mutating back at "+tomutate);
                        Q1.mutate(tomutate,1);
                    }
                }
                Quiver Q2=S.Q.copy();
                System.out.println("Deleting "+v2);
                Q2.deletenode(v2);
                if (sc!=null){
                    for (int i=sc.length-2;i>=0;i--){
                        if (sc[i]<=v2){
                            tomutate=sc[i];
                        }
                        else {
                            tomutate=sc[i]-1;
                        }
                        //System.out.println("Mutating back at "+tomutate);
                        Q2.mutate(tomutate,1);
                    }
                }
                int todelete;
                if (v1<v2){
                    todelete=v1;
                }
                else{
                    todelete=v1-1;
                }
                System.out.println("Deleting "+todelete);
                Q2.deletenode(todelete);

                AbstractQuiver aq;
                if (qs.size()==0){
                   aq=new AbstractQuiver(S.Q, this, true);
                   aq.embeddedQuiver.scalecenter(getBounds());
                   qs.add(aq); 
               }

               //qs.removeCurrent();

               aq=new AbstractQuiver(Q1, this, true);
               aq.embeddedQuiver.scalecenter(getBounds());
               qs.add(aq); 

               aq=new AbstractQuiver(Q2,this,true);
               aq.embeddedQuiver.scalecenter(getBounds());
               qs.add(aq); 


               S.Q=qs.next().getQuiver(this);
               S.Q.Hist.updatebuttons();
               qs.removePreceding();
                     
                
            } 
            else {
               S.Q=qs.next().getQuiver(this);
               S.Q.Hist.updatebuttons();  
            }
            updatetoolmenu();
            updatePaneAndMenu();
            updatestatus(status);
        }
        }
    }
        
    else if (actionCommand.equals("Quantum monomial ...")){
     String str=JOptionPane.showInputDialog(this,"Computing the Q-power M(e).\n"   
     + "Enter the components of e separated by spaces");
     if ((str!=null) && (str!="")){
                   String[] fields=str.split(" ");
                   BigInteger[] e=new BigInteger[fields.length];
                   for (int i=0; i<fields.length; i++){
                      e[i]= new BigInteger(fields[i]);
                   }
                   Polynomial P=myQxv.M(S.Q.Lambda,e);
                   TextDisplayDialog.showDialog(this,this,"Q-power", P.toString(),10, 60);
                }
    }
    else if (actionCommand.equals("Test2 ...")){
     //String s=JOptionPane.showInputDialog(this,"Looking for sinks or sources.\n" 
     //+ "Enter maximal number of random mutations to perform (1-10000)", "500");
        String s="500";
        if (s!=null){
            int nber=Integer.parseInt(s);
            if ((0<nber)&(nber<10001)){
                int i=0;
                boolean found=(S.Q.sinksSources().length>0);
                while ((i<=nber)&(!found)){
                    int r=MyRandom.nextInt(S.Q.nbpoints);
                    S.mutate(r);
                    found=(S.Q.sinksSources().length>0);
                    if (!found && S.Q.maxMultExceeds(3)){
                        S.mutate(r);
                    }
                    else {
                    S.Q.Hist.add(r);
                    }
                    
                    i++;
                }
                if (found){
                    int[] ss=S.Q.sinksSources();
                    s="";
                    if (ss.length==0){
                        s="None";
                    }
                    else{
                        for (int j=0; j<ss.length-1;j++){
                            s=s+(ss[j]+1)+", ";
                        }
                        s=s+(1+ss[ss.length-1]);
                        }
                                JOptionPane.showMessageDialog(this,"Found a sink/source in "+(i-1)+" steps: " +s);
                        }
                     else {
                           JOptionPane.showMessageDialog(this,"Did not find a sink/source in "+(i-1)+" steps.");
                        }
                  }
          
        }
    }
    else if (actionCommand.equals("Replay")){
        if (replayTimer==null){
            ReplayItem.setSelected(true);
            replayTimer=new javax.swing.Timer(replayspeed,this);
            replayTimer.start();
            //System.out.println("ShakeTimer set.");
            replaydir=-1;
            replayphase=0;
        }
        else
        {
            ReplayItem.setSelected(false);
            if (replayTimer!=null){replayTimer.stop();}
            replayTimer=null;
            //System.out.println("ShakeTimer stopped");
        }
    }
    
    else if (actionCommand.equals("Random")){
        if (randomTimer==null){
            RandomItem.setSelected(true);
            randomTimer=new javax.swing.Timer(replayspeed,this);
            randomTimer.start();
            //System.out.println("randomTimer set.");
            replayphase=0;
        }
        else
        {
            RandomItem.setSelected(false);
            if (randomTimer!=null){randomTimer.stop();}
            randomTimer=null;
            //System.out.println("randomTimer stopped");
        }
    }
    
    else if (actionCommand.equals("Speed ...")){
       String s=JOptionPane.showInputDialog("Enter delay in milliseconds",""+replayspeed);
        if (s!=null){
        int nber=Integer.parseInt(s);
        if ((0<nber)&(nber<10000)){
            replayspeed=nber;
            if (replayTimer!=null){ 
                replayTimer.setDelay(replayspeed);
            }
            if (randomTimer!=null){ 
                randomTimer.setDelay(replayspeed);
            }
        }
    }}
    
    else if (actionCommand.equals("Faster")){
        if (replayspeed>40){replayspeed=5*replayspeed/7;}
        if (replayTimer!=null){ 
                replayTimer.setDelay(replayspeed);
            }
        if (randomTimer!=null){ 
                randomTimer.setDelay(replayspeed);
            }
    }
    
    else if (actionCommand.equals("Slower")){
        if (replayspeed<4000){replayspeed=7*replayspeed/5;}
        if (replayTimer!=null){ 
                replayTimer.setDelay(replayspeed);
            }
        if (randomTimer!=null){ 
                randomTimer.setDelay(replayspeed);
            }
    }
    else if (actionCommand.equals("Swap memory")){
        S.swapQmem();   
    }
    else if (actionCommand.equals("Triangle product")){
        if (S.Qmem==null) {
            JOptionPane.showMessageDialog(this, "No quiver in memory");
        }
        else {
            SequencesDialog sd=new SequencesDialog(this);
            S.product(sd);   
        }
    }
    else if (actionCommand.equals("CY-dimension ...")){
        String s=JOptionPane.showInputDialog(this,"Enter the Calabi-Yau dimension (2 to 20)", ""+S.Q.getClusterdim());
        if (s!=null){
            int cd=Integer.parseInt(s);
            if ((2<=cd) & (cd<=20)){
                System.out.println("cd="+cd);
                S.Q.setClusterdim(cd);
            }
        }
    }
    else if (actionCommand.equals("Decompose ...")){
        //TextDisplayDialog.showDialog(fr,fr,"q-y-system", S.Q.qysystem(),5,40);
        String s=JOptionPane.showInputDialog(this, "Enter vector to decompose with commas (no spaces!) between" +
                " the components");
        if (s!=null){
            if (s.length()>0){
                String[] fields = s.split(",");
                int n=fields.length;
                BigInteger[] v=new BigInteger[n];
                for (int i=0; i<n;i++){
                    v[i]=new BigInteger(""+Integer.parseInt(fields[i]));
                }
                Dispvector dv=(Dispvector) S.getMutable("dim (1)");
                if (dv==null){
                    JOptionPane.showMessageDialog(this, "Before decomposing a vector, you need to compute a " +
                            "list of dimension vectors using the Dimensions item.");
                }
                if (dv!=null){
                TextDisplayDialog.showDialog(fr,fr,"Decompositions", dv.decompVector(v),5,40);
                }
            }
        }
    }
    else if (actionCommand.equals("Random w-quiver")){
        Permutation P=Permutation.rand(S.Q.nbpoints);
        //JOptionPane.showMessageDialog(this, "Permutation: "+ P+ "\nReduced word: "+P.redword());
        int[] sq=Utils.StringToIntArray(P.redword());
        S.removeAllMutables();
        S.Q.initializeWordquiver();
        double x,y;
        double tx=30;
        double ty=0;
        for (int i=0; i<sq.length; i++){
            x=S.Q.P[sq[i]].x+(i+1)*tx;
            y=S.Q.P[sq[i]].y+(i+1)*ty;
            S.addwpoint((int) Math.round(x), (int) Math.round(y));
        }
        S.Q.finalizeWordquiver();


            String[] seq=new String[2];
            seq[0]=S.Q.getWordquiverTaumutseq();
            seq[1]=S.Q.getWordquiverTauperm();
            SequencesDialog sd=new SequencesDialog(this, false);
            sd.setSequences(seq);
            S.Q.setSeqDia(sd);
            S.Q.SeqDia.setVisible(false);
            int bound=100;

            int order=S.mutorder(seq[0],seq[1],bound);
            if (order>-1){
                    JOptionPane.showMessageDialog(this,"Order="+order);
                    }
                else {
                   JOptionPane.showMessageDialog(this,"Order > "+bound);
                }

    }
    else if (actionCommand.equals("Test3 ...")){
     String s=JOptionPane.showInputDialog(this,"Looking for sinks or sources.\n" 
     + "Enter maximal depth, maximal multiplicity in the format a,b (no spaces)", "5,3");
     if (s!=null){
          String[] fields = s.split(",");
          //System.out.println("a="+fields[0]+" b="+fields[1]);
            int maxDepth=Integer.parseInt(fields[0]);
            int maxMult=Integer.parseInt(fields[1]);
            SourceSinkSearcher mySearcher=new SourceSinkSearcher(maxDepth, maxMult, S.Q);
            String st="";
            mySearcher.traverseTree(maxDepth,-1,st,S.Q.nbpoints);
            boolean found=(S.Q.sinksSources().length>0);
            if (found){
                int[] ss=S.Q.sinksSources();
                s="";
                if (ss.length==0){
                    s="None";
                }
                else{
                    for (int j=0; j<ss.length-1;j++){
                        s=s+(ss[j]+1)+", ";
                    }
                    s=s+(1+ss[ss.length-1]);
                    }
                            JOptionPane.showMessageDialog(this,"Found a sink/source: " +s);
                }
             else {
                   JOptionPane.showMessageDialog(this,"Did not find a sink/source.");
             }
        }
    }
    else if (actionCommand.equals("d-polyhedron ...")){
        Dvector dv=new Dvector(S.Q.nbpoints);
        myTracker=new Tracker(S.Q.nbpoints,dv,S.Q.M);
        
        //S.addMutable(myTracker);
	//updatePaneAndMenu();
        
        String s=JOptionPane.showInputDialog(this, "Enter depth of transversal");
        int n=Integer.parseInt(s);
        s="";
        Quiver q=new Quiver(S.Q.M,this);
        myTracker.traverseTree(n,-1,s,q);
        
        TextDisplayDialog.showDialog(fr,fr,"jvx", myTracker.tojvx(),5,40);
    }
    
    else if (actionCommand.equals("Dual d-polyhedron ...")){
        Dvector dv=new Dvector(S.Q.nbpoints);
        myTracker=new Tracker(S.Q.nbpoints,dv,S.Q.M);
        myTracker.setNoCoordTrsf();
        
        //S.addMutable(myTracker);
	//updatePaneAndMenu();
        
        String s=JOptionPane.showInputDialog(this, "Enter depth of transversal");
        int n=Integer.parseInt(s);
        s="";
        Quiver q=new Quiver(S.Q.M,this);
        myTracker.traverseTree(n,-1,s,q);
        
        
        //myTracker.setNoCoordTrsf();
        TextDisplayDialog.showDialog(fr,fr,"jvx", myTracker.dualtojvx(),5,40);
    }
    
    else if (actionCommand.equals("d-poly. (unnormed) ...")){
        Dvector dv=new Dvector(S.Q.nbpoints);
        myTracker=new Tracker(S.Q.nbpoints,dv,S.Q.M);
        myTracker.setNoCoordTrsf();
        
        //S.addMutable(myTracker);
	//updatePaneAndMenu();
        
        String s=JOptionPane.showInputDialog(this, "Enter depth of transversal");
        int n=Integer.parseInt(s);
        s="";
        Quiver q=new Quiver(S.Q.M,this);
        myTracker.traverseTree(n,-1,s,q);
        
        TextDisplayDialog.showDialog(fr,fr,"jvx", myTracker.tojvx(),5,40);
    }
    
    else if (actionCommand.equals("g-polyhedron ...")){
        Gvector gv=new Gvector(S.Q.nbpoints,S.Q.M);
        myTracker=new Tracker(S.Q.nbpoints,gv,S.Q.M);
        //myTracker.setNoCoordTrsf();
        
        //S.addMutable(myTracker);
	//updatePaneAndMenu();
        
        String s=JOptionPane.showInputDialog(this, "Enter depth of transversal");
        int n=Integer.parseInt(s);
        s="";
        Quiver q=new Quiver(S.Q.M,this);
        myTracker.traverseTree(n,-1,s,q);
        
        TextDisplayDialog.showDialog(fr,fr,"jvx", myTracker.tojvx(),5,40);
    }
    
    else if ((actionCommand.equals("Live quivers")) || (actionCommand.equals("Static quivers"))){
        if (lapsetimer==null){
          lapsetimer = new javax.swing.Timer(timeLapse, this);
          lapsetimer.start();}
        else{
            lapsetimer.stop();
            lapsetimer=null;
        }
        updatetoolmenu();
    } 
    else if (actionCommand.equals("Parameters ...")){
        ParameterDialog pd=new ParameterDialog(fr, this);
    }
    else if (actionCommand.equals("Export to Sage ...")){
        String s=S.Q.M.toSageMatrix();
        TextDisplayDialog.showDialog(fr, fr, "Sage matrix", s, 25,40);
    }
    else if (actionCommand.equals("Export to Maple ...")){
        String s=S.Q.M.toMapleMatrix();
        TextDisplayDialog.showDialog(fr, fr, "Maple matrix", s, 25,40);
    }
    else if (actionCommand.equals("Clear sequences")){
        if (S.Q.getSeqDia()!=null){
          S.Q.getSeqDia().setVisible(false);
        }
        S.Q.setSeqDia(null);
    }
    else if (actionCommand.equals("Sequences ...")){
      if (S.Q.getSeqDia()!=null){
          S.Q.getSeqDia().setVisible(true);
        }
      else {
        SequencesDialog sd=new SequencesDialog(this);
        String taumutseq=S.Q.getWordquiverTaumutseq();
        String tauperm=S.Q.getWordquiverTauperm();
        System.out.println("Taumutseq and Tauperm:" + taumutseq + " p "+tauperm);
        if ((taumutseq!=null) && (tauperm!=null)){
            sd.setSequence(taumutseq);
            sd.setPerm(tauperm);
            S.Q.setSeqDia(sd);
        }
        S.Q.setSeqDia(sd);
        }
    }
    repaint();
    }

    public void mouseMoved(MouseEvent me) { }
    
    private BigInteger dimHom(int l, int m){
        int l1=l/meshlength;
        int l2=l-l1*meshlength;
        int m1=m/meshlength;
        int m2=m-m1*meshlength;
        //System.out.println("l1, l2 : " + l1 + " , " + l2);
        //System.out.println("m1, m2 : "+ m1 + " , " + m2);
        if (m1<l1){
            return BigInteger.ZERO;
        }
        else {
            return S.Q.dimVect.A[m-l1*meshlength][l2];
        }
    }
    
    private BigInteger dimHomTau(int l, int m){
        int tm=m-meshlength;
        if (tm<0){
            return BigInteger.ZERO;
        }
        else {
            return dimHom(l, tm);
        }
    }
    
    public BigInteger dimHomTauminus(int l, int m){
        int tm=m-meshlength;
        if (tm<0){
            return BigInteger.ZERO;
        }
        else {
            return dimHom(l, tm);
        }
    }
    
    private boolean isCompat(int l, int m){
        boolean compat=true;
        if (dimHomTau(l,m).compareTo(BigInteger.ZERO)!=0){
            compat=false;
        }
        if (dimHomTau(m,l).compareTo(BigInteger.ZERO)!=0){
            compat=false;
        }
        return compat;
    }
    
    private boolean isExcCompat(int l, int m){
        boolean compat=true;
        if (dimHom(l,m).compareTo(BigInteger.ZERO)!=0){
            compat=false;
        }
        if (dimHomTauminus(m,l).compareTo(BigInteger.ZERO)!=0){
            compat=false;
        }
        return compat;
    }

    public void mouseClicked(MouseEvent me) {
    boolean found;
    int x,y;
    
    boolean altdown=((me.getModifiersEx() & MouseEvent.ALT_DOWN_MASK)==MouseEvent.ALT_DOWN_MASK);
    boolean ctrldown=((me.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK)==MouseEvent.CTRL_DOWN_MASK);
    boolean shiftdown=((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK);
    
    if (altdown){
        System.out.println("alt-Click");
    }
    switch (status){
    case WAIT_FOR_BANFF_BRANCH_1:
       found=false;
       int deletepoint=0;
       for (int i=0; i<S.Q.nbpoints; i++){
             if (S.Q.P[i].hit(me.getX(), me.getY())){
                 found=true;
                 deletepoint=i;
             }
         }
         if (found){
                 //System.out.println("First vertex to merge: " + (firstMergePoint+1));
                 AbstractQuiver aq;
                   if (qs.size()==0){
                       aq=new AbstractQuiver(S.Q, this, true);
                       aq.embeddedQuiver.scalecenter(getBounds());
                       qs.add(aq); 
                   }
                   
                   Quiver q=S.Q.copy();
                   q.deletenode(deletepoint);
                   aq=new AbstractQuiver(q, this, true);
                   aq.embeddedQuiver.scalecenter(getBounds());
                   qs.add(aq); 
                   
                   updatetoolmenu();
                   updatestatus(WAIT_FOR_BANFF_BRANCH_2);
             }
    break;
    
    case WAIT_FOR_BANFF_BRANCH_2:
       found=false;
       deletepoint=0;
       for (int i=0; i<S.Q.nbpoints; i++){
             if (S.Q.P[i].hit(me.getX(), me.getY())){
                 found=true;
                 deletepoint=i;
             }
         }
         if (found){
                 //System.out.println("First vertex to merge: " + (firstMergePoint+1));
                   Quiver q=S.Q.copy();
                   q.deletenode(deletepoint);
                   AbstractQuiver aq=new AbstractQuiver(q, this, true);
                   aq.embeddedQuiver.scalecenter(getBounds());
                   qs.add(aq); 
                   S.Q=qs.next().getQuiver(this);
                   S.Q.Hist.updatebuttons();
                   //qs.removePreceding();
                   updatetoolmenu();
                   updatePaneAndMenu();
                   updatestatus(MUTATING);
             }
    break;
            
    case MUTATING:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
             
             if (altdown&&(!ctrldown)){
                 S.deletepoint(i);
                 repaint(0);
                 break;
             }
          //System.out.println("Click at " + me.getX() + 
              //                             " " + me.getY() + " in " + i);
             if (ctrldown){
                if (altdown){
                S.Q.P[i].height=S.Q.P[i].height-gridsize;
                S.Q.P[i].showHeight();
                    }
                else {
                    S.Q.P[i].height=S.Q.P[i].height+gridsize;
                    S.Q.P[i].showHeight();
                    }
                }
            else if (!S.Q.P[i].frozen){
                if (mutsound){mutClip.play();}
                if (altdown){
                    System.out.println("Alt pressed.");
                    S.mutate(i,-1);
                }
                else {
                    S.mutate(i);
                }
                S.Q.Hist.add(i);
            }
            repaint();
         }
        }
      break;
    case ADD_NODES:
        x=me.getX();
        y=me.getY();
        if (shiftdown){
            x=Math.round(((float) x)/gridsize)*gridsize;
            y=Math.round(((float) y)/gridsize)*gridsize;
        }
        S.addpoint(x, y);
        repaint();
        break;
    case ENTER_W_QUIVER:
        x=me.getX();
        y=me.getY();
        if (shiftdown){
            x=Math.round(((float) x)/gridsize)*gridsize;
            y=Math.round(((float) y)/gridsize)*gridsize;
        }
        S.addwpoint(x, y);
        repaint();
        break;
    case ENTER_DOUBLE_W_QUIVER:
        x=me.getX();
        y=me.getY();
        if (shiftdown){
            x=Math.round(((float) x)/gridsize)*gridsize;
            y=Math.round(((float) y)/gridsize)*gridsize;
        }
        S.adddoublewpoint(x, y,altdown);
        repaint();
        break;
    case WAIT_FOR_NEW_SOURCE:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
         sourceindex=i;
         //System.out.println("Source vertex: " + i);
         updatestatus(WAIT_FOR_NEW_TARGET);}
      }
    break;
   
    case WAIT_FOR_NEW_TARGET:
          for (int i=0; i<S.Q.nbpoints; i++){
            if (S.Q.P[i].hit(me.getX(), me.getY())){
	     //System.out.println("Target vertex: " + i);
         S.Q.addarrow(sourceindex,i);
         repaint(10);
         updatestatus(WAIT_FOR_NEW_SOURCE);
         }
          }
          break;
    
    case DEL_NODES:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
	     //System.out.println("Vertex to delete: " + i);
         S.deletepoint(i);
         repaint(10);
         }
      }
      break;
    case WAIT_FOR_MERGE_1:
         found=false;
         for (int i=0; i<S.Q.nbpoints; i++){
             if (S.Q.P[i].hit(me.getX(), me.getY())){
                 found=true;
                 firstMergePoint=i;
             }
         }
         if (found){
                 //System.out.println("First vertex to merge: " + (firstMergePoint+1));
                 updatestatus(WAIT_FOR_MERGE_2);
             }
      break;
    case WAIT_FOR_MERGE_2:
         int i2=0;
         found=false;
         for (int i=0; i<S.Q.nbpoints; i++){
            if (S.Q.P[i].hit(me.getX(), me.getY())){
                found=true;
                i2=i;
                //System.out.println("Second vertex to merge: " + (i2+1));
            }
         }
         if (found){
             int i1=firstMergePoint;
             S.addpoint(me.getX(),me.getY());
             S.Q.mergevertices(i1,i2);
             S.deletepoint(i1);
             if (i1>i2)  {
                 S.deletepoint(i2);
             }
             else {
                 S.deletepoint(i2-1);
             }
             repaint(10);
             updatestatus(WAIT_FOR_MERGE_1);
         }
      break;
    case FREEZE_NODES:
          for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
         //System.out.println("Vertex to (un)freeze: " + i);
         S.Q.P[i].togglePhase();
         }
      }
      break;
      case COLOR_NODES:
          for (int i=0; i<S.Q.nbpoints; i++){
             if (S.Q.P[i].hit(me.getX(), me.getY())){
             //System.out.println("Vertex to (un)freeze: " + i);
             S.Q.P[i].setColor(newNodeColor);
             }
          }
      break;
     case WAIT_FOR_NEW_VSOURCE:
          for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
         sourceindex=i;
         //System.out.println("Source vertex: " + i);
         updatestatus(WAIT_FOR_NEW_VTARGET);}
         }
    break;
         
    case WAIT_FOR_STYLE_SOURCE:
          for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
         sourceindex=i;
         //System.out.println("Source vertex: " + i);
         updatestatus(WAIT_FOR_STYLE_TARGET);}
         }
    break;
   
    case WAIT_FOR_NEW_VTARGET:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
	     //System.out.println("Target vertex: " + i);
         String s=(String)JOptionPane.showInputDialog("Enter valuation in the format a,b (no spaces):");
         //System.out.println(s);
         String[] fields = s.split(",");
         //System.out.println("a="+fields[0]+" b="+fields[1]);
         S.Q.addvaluedarrow(sourceindex,i, Integer.parseInt(fields[0]),Integer.parseInt(fields[1]));
         BMatrix D=Utils.antisymmetrizingDiag(S.Q.M);
            if (D==null){
                JOptionPane.showMessageDialog(this,"Warning: This valued quiver does not correspond to " +
                        "an antisymmetrizable matrix!");
            }
            else System.out.println(D);
         repaint(10);
         updatestatus(WAIT_FOR_NEW_VSOURCE);
         }
     }
     break;
        
     case WAIT_FOR_STYLE_TARGET:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
	     //System.out.println("Target vertex: " + i);
         S.Q.setArrowStyle(sourceindex, i, chosenArrowStyle);
         System.out.println(S.Q.StyleMatrix.toString());
         repaint(10);
         updatestatus(WAIT_FOR_STYLE_SOURCE);
         }
     }
     break;
     
     
     case WAIT_FOR_TILTING_SUMMAND:
         for (int l=0; l<S.Q.nbpoints; l++){
             if (S.Q.P[l].hit(me.getX(), me.getY())){ tiltingSummand[l]=!tiltingSummand[l];}
         }
         for (int m=0; m<S.Q.nbpoints; m++){S.Q.P[m].setColor(Color.GREEN);}
         for (int l=0; l<S.Q.nbpoints; l++){
             if (tiltingSummand[l]){
             for (int m=0; m<S.Q.nbpoints; m++){
                 if (!isCompat(l,m)){
                       S.Q.P[m].setColor(Color.RED);
                     }
                }
            }
         }
         for (int l=0; l<S.Q.nbpoints;l++){
                 if (tiltingSummand[l]){
                     S.Q.P[l].setColor(Color.BLUE);
                 }
          }
         repaint();
         
     break;
     
     case WAIT_FOR_EXC_SUMMAND:
         for (int l=0; l<S.Q.nbpoints; l++){
             if (S.Q.P[l].hit(me.getX(), me.getY())){ tiltingSummand[l]=!tiltingSummand[l];}
         }
         for (int m=0; m<S.Q.nbpoints; m++){S.Q.P[m].setColor(Color.GREEN);}
         for (int l=0; l<S.Q.nbpoints; l++){
             if (tiltingSummand[l]){
             for (int m=0; m<S.Q.nbpoints; m++){
                 if (!isExcCompat(m,l)){
                       S.Q.P[m].setColor(Color.RED);
                     }
                }
            }
         }
         for (int l=0; l<S.Q.nbpoints;l++){
                 if (tiltingSummand[l]){
                     S.Q.P[l].setColor(Color.BLUE);
                 }
          }
         repaint();
         
     break;
     
     case WAIT_FOR_NEXT_NODE:
         for (int i=0; i<S.Q.nbpoints; i++){
         if (S.Q.P[i].hit(me.getX(), me.getY())){
         //System.out.println("Vertex to (un)freeze: " + i);
         if (!S.Q.P[i].marked){
             S.Q.P[i].marked=true;
             S.Q.P[i].setLabel(""+(nodenber+1));
             nodeseq[nodenber]=i;
             nodenber++;
            }
         }
         if (nodenber==S.Q.nbpoints){
             for (i=0; i<S.Q.nbpoints; i++){
                 S.Q.P[i].marked=false;
                 S.Q.P[i].deleteLabel();
                }
             if (heightsBeingShown){
                 S.Q.showHeights();
             }
             int[] invnodeseq= S.invPerm(nodeseq);
             String s=""+nodeseq[0];
             String t=""+invnodeseq[0];
             for (i=1; i<S.Q.nbpoints;i++){
                 s=s+","+nodeseq[i];
                 t=t+","+invnodeseq[i];
             }
             //JOptionPane.showMessageDialog(this, "Node sequence: "+ s + "\nInverse sequence:"+t);
             S.permuteNodes(nodeseq);
             S.Q.permuteVertexPositions(nodeseq);
             updatestatus(MUTATING);
             }             
         else{
             updatestatus(WAIT_FOR_NEXT_NODE);
         }
         }
          repaint();
     break;
         }
      }
  
    
    
    
 
    public void mousePressed(MouseEvent me) {
    for (int i = 0; i < S.Q.nbpoints; i++) {
        if (S.Q.P[i].hit(me.getX(), me.getY())) {
        movingPoint = S.Q.P[i];  
        movingPoint.fixed=true;
        movePoint(me.getX(), me.getY());
        return;
        }
    }
    }
    
    public void mouseReleased(MouseEvent me) {
        int x=me.getX();
        int y=me.getY();
        if ((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK){
            x=Math.round(((float) x)/gridsize)*gridsize;
            y=Math.round(((float) y)/gridsize)*gridsize;
        }
    movePoint(x,y);
    if (movingPoint!=null){
        movingPoint.dx=0;
        movingPoint.dy=0;
        movingPoint.fixed=false;
    }
    movingPoint = null;
    }

    public void mouseDragged(MouseEvent me) {
        int x=me.getX();
        int y=me.getY();
        if ((me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK){
            x=Math.round(((float) x)/gridsize)*gridsize;
            y=Math.round(((float) y)/gridsize)*gridsize;
        }
        movePoint(x,y);
    }    

    void movePoint(int x, int y) {
    if (movingPoint == null) return;
    if (contains(x,y)){
	movingPoint.setLocation(x, y);
	repaint();}
    }


    public void mouseEntered(MouseEvent me) { }
    public void mouseExited(MouseEvent me) { }


    public void paintComponent(Graphics gfx) {
    super.paintComponent(gfx);         // standard setup
    Graphics2D g = (Graphics2D) gfx;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON); 
   
     if (hasGrid){
            Rectangle r=getBounds();
            int x0=r.x-(r.x % gridsize);
            int y0=r.y-(r.y % gridsize);
            int x1=x0+r.width+gridsize;
            int y1=y0+r.height+gridsize;
            int x=x0;
            g.setColor(Color.GRAY);
            while (x<x1){
               g.drawLine(x,y0,x,y1);
               x=x+gridsize;
            }
            int y=y0;
            while (y<y1){
                g.drawLine(x0,y,x1,y);
                y=y+gridsize;
            }
         }
         if (S.Q.simples!=null){
             S.Q.recenter();
             S.Q.colorStables();
         }
         S.Q.drawQuiver(g);
     }

    
    
    public void setFrame(Frame fra){
        fr=fra;
    }
    
    public void setHeightItem(JMenuItem mi){
        heightItem=mi;
    }
    
    public void setGridItem(JMenuItem mi){
        gridItem=mi;
    }
    
    public void setShowNumbersItem(JMenuItem mi){
        showNumbersItem=mi;
    }
    
    public void setShortnumbersItem(JMenuItem mi){
        shortNumbersItem=mi;
    }

    public void setTrafficLightsItem(JMenuItem mi){
        trafficLightsItem=mi;
    }
    
    public void setShowFrozenVerticesItem(JMenuItem mi){
        showFrozenVerticesItem=mi;
    }
    public void setLambdaQuiverItem(JMenuItem mi){
        lambdaQuiverItem=mi;
    }
    
    public void setFontSizeItem(JMenuItem mi){
        fontSizeItem=mi;
    }
    
    public void setCartanItem(JMenuItem di){
        CartanItem=di;
    }
    
     public void setMutSoundItem(JCheckBoxMenuItem di){
        MutSoundItem=di;
    }
     
      public void setBkgrSoundItem(JCheckBoxMenuItem di){
        BkgrSoundItem=di;
    }
      
     public void setReplayItem(JCheckBoxMenuItem di){
        ReplayItem=di;
    }
     
     public void setRandomItem(JCheckBoxMenuItem di){
        RandomItem=di;
    }
     
     public void setCentralChargeItem(JMenuItem di){
        CentralChargeItem=di;
    }
     
     public void setSubmodulesItem(JMenuItem di){
        SubmodulesItem=di;
    }
     
     public void setSpikesItem(JMenuItem di){
        SpikesItem=di;
    }
    
    
    
    
    public void setClusterMenu(JMenu me){
        clusterMenu=me;
    }
    
    public void setDeactivateMenu(JMenu me){
        deactivateMenu=me;
    }

    public QuiverDrawing(MutationApp ma, JLabel sl, JMenuItem mi, JButton backb, JButton forwardb, JSplitPane sp, int applT){
    //q=new Quiver("GLSA4.txt");
    //String s = (String)JOptionPane.showInputDialog("Enter rank");
    //q=new Quiver(Quiver.GLSAN,Integer.parseInt(s));
    
    URL ClipURL=null;
    try {
        ClipURL = this.getClass().getClassLoader().getResource ("audio/drip.au");
        if (ClipURL!=null){System.out.println ("Found mutation sound at " + ClipURL);}
    } catch (Exception e) {
            e.printStackTrace();
    }
    if (ClipURL!=null){mutClip=Applet.newAudioClip(ClipURL);}
    mutsound=false;
    
    try {
        ClipURL = this.getClass().getClassLoader().getResource ("audio/mario_kart_64.mid");
        if (ClipURL!=null){System.out.println ("Found background sound at " + ClipURL);}
    } catch (Exception e) {
            e.printStackTrace();
    }
    if (ClipURL!=null){bkgrClip=Applet.newAudioClip(ClipURL);}
    bkgrsound=false;
    
    
    backbutton=backb;
    forwardbutton=forwardb;
    S=new Seed(Quiver.GLSAN,4, this);
    S.Q.Hist.updatebuttons();
    LamQuiverDrawing=null;
    addMouseListener(this);
    addMouseMotionListener(this);
    addComponentListener(this);
    qs=new QuiverSet(this);
    lastReadLine=null;
    statusLabel=sl;     
    showLabelsItem=mi;
    hasGrid=false;
    heightsBeingShown=false;
    //history=new Vector(10,1);
    //historycounter=-1;
    clusterMenu=null; //Is set later in main.
    toolMenu=null; //Is set later in main.
    lapsetimer=null;
    muttimer=null;
    replayTimer=null;
    randomTimer=null;
    splitPane=sp;
    splitPane.setBottomComponent(null);
    applType=applT;
    if (applType==APPLICATION){
      fc=new JFileChooser();
      fc.addChoosableFileFilter(new MutationFilter());
      fileName="";
      
      soundfc=new JFileChooser();
      soundfc.addChoosableFileFilter(new SoundFilter());
    }
    MySeed=new Long(0);
    MyRandom=new Random();
    randnber=30;
    gridsize=25;
    friezenber=5;
    edgeLength=ParameterDialog.LENGTH_DEFAULT;
    timeLapse=ParameterDialog.LAPSE_DEFAULT;
    replayspeed=1400;
    replaypause=0;
    updatestatus(MUTATING);
    }

}

class ValencySequence implements Comparable{
    BigInteger[] X;
    int length;
    int pos;
    
     private int comparecomponent(int i, ValencySequence M){
        int res=0;
        if ((i>=length) && (i>=M.length)){
        res=0;
        }
        if ((i>=length) && (i<M.length)){
            res=-1;
        }
        if ((i<length) && (i>=M.length)){
            res=1;
        }
        if ((i<length) && (i<M.length)){
            res=X[i].compareTo(M.X[i]);
        }
        return res;
     }

    public int compareTo(Object o){
        int res;
        ValencySequence M;
        
        M=(ValencySequence) o;
        int i=0;
        int L=Math.max(length,M.length);
        res=comparecomponent(i,M);
        while ((res==0) && (i<L)){
            i++;
            res=comparecomponent(i,M);
            //System.out.println("i:"+i+" comp:"+res);
        }
        return res;
    }

    public ValencySequence(BigInteger[] s, int argpos){
	X=s;
	length=X.length;
        pos=argpos;
    }
    
    /*
    public boolean equals(Object o){
        ValencySequence v=(ValencySequence) o;
        boolean eq=true;
        if (X.length!=v.X.length){eq=false;}
        int i=0;
        while (eq && (i<X.length)){
            if (!X[i].equals(v.X[i])){eq=false;}
            i++;
        }
        return eq;
    }
     */


    public ValencySequence(String [] argX, int argLength, int argpos){
	pos=argpos;
	length=argLength;
	X=new BigInteger[length];
	for (int i=0; i<length; i++){
	    X[i]=new BigInteger(argX[i]);
	}
    }

    public String toString(){
	String s=""+pos+": "+X[0];
	for (int i=1; i<length; i++){
	    s=s+","+X[i];
	}
	return s;
    }
}

class Halfarrow {
    int vertex;
    BigInteger v1, v2;
    
    public Halfarrow(int argv, BigInteger argv1, BigInteger argv2){
        vertex=argv;
        v1=argv1;
        v2=argv2;
    }
    public String toString(){
        return ""+vertex+" "+v1+" "+v2;     
    }

   public static boolean equalval(Halfarrow ha1, Halfarrow ha2){
       boolean res;
       if (((ha1==null) && (ha2!=null)) || ((ha1!=null) && (ha2==null))){
	   res=false;
       }
       else if ((ha1==null) && (ha2==null)){
	   res=true;
       }
       else {
	   res=((ha1.v1.equals(ha2.v1)) && (ha1.v2.equals(ha2.v2)));
       }
       return res;
   }
}

class Vertex{
    boolean frozen;
    BigInteger[] Valencies;
    Halfarrow[] Starting; // set of Halfarrow: (targetvertex, arrowvaluation)
    Halfarrow[] Ending; // set of Halfarrow: (sourcevertex, arrowvaluation)
    
    public int nbarrowsto(int v2){
        int res=0;
        for (int i=0; i<Starting.length; i++){
            if (Starting[i].vertex==v2){res++;}
        }
        return res;
    }
    
    public boolean hasMultArrowStarting(){
        boolean hma=false;
        for (int i=0; i<Starting.length; i++){
            int b1=Starting[i].v1.abs().compareTo(BigInteger.ONE);
            int b2=Starting[i].v2.abs().compareTo(BigInteger.ONE);
            if ((b1>0)||(b2>0)){hma=true;}
        }
        return hma;
    }

    public Halfarrow halfarrowto(int v2){
	Halfarrow ha=null;
	boolean found=false;
	int i=-1;
	while ((i<Starting.length-1) && (!found)){
	    i++;
	    if (Starting[i].vertex==v2){found=true; ha=Starting[i];}
	}
	return ha;
    }
   
    public Vertex(BigInteger[] argvalencies, int nbstart, int nbend){
        Valencies=argvalencies;
        Starting=new Halfarrow[nbstart];
        Ending=new Halfarrow[nbend];
    }
    
    public boolean HasEqualValencies(Vertex v){
        boolean eq=true;
        if (Valencies.length!=v.Valencies.length){eq=false;}
        int i=0;
        while (eq && (i<Valencies.length)){
            if (!Valencies[i].equals(v.Valencies[i])){eq=false;}
            i++;
        }
        return eq;
    }
    
    public String toString(){
        String s="Valencies: ";
        for (int i=0; i<Valencies.length; i++){
            s=s+" "+Valencies[i];
        }
        s=s+"\n"+"Starting: ";
        for (int i=0; i<Starting.length; i++){
            if (i>0){s=s+",";}
            s=s+Starting[i];
        }
        s=s+"\n"+"Ending: ";
        for (int i=0; i<Ending.length; i++){
            if (i>0){s=s+",";}
            s=s+Ending[i];
        }
        return s;
    }
}

class AbstractQuiver {
    // List of categories whose objects can be compared in compare()
    public static final int GRAPH=0;
    public static final int SINK_SOURCE=1;
    public static final int QUIVER=2; 
    public static final int QUIVER_MOD_ICE=3;
    public static int[] lastfoundisom;
        
    
    Vertex[] Vertices;
    AbstractQuiver underlyingGraph;
    Quiver embeddedQuiver;
    QuiverSet sinkSourceEqSet; // set of abstract quivers which are sink-source equivalent to this one
    QuiverDrawing qd;
    int category; // determines the category to be used in equals()
    BigInteger weight; // weight of the matrix
    int[] hist;
    int[] perm; // permutation of vertices with respect to matrix used in construction

   public static AbstractQuiver emptyAbstractQuiver(){
       return null;    
   }
    
    public AbstractQuiver deleteFrozenNodes(){
        Quiver q=getQuiver(qd);
        Quiver qn=new Quiver(q.M,qd);
        qn.deleteFrozenNodes();
        return new AbstractQuiver(qn,qd);
    }
    
    public QuiverSet getSinkSourceEqSet(QuiverDrawing qd){
        if (sinkSourceEqSet==null){
            Quiver q=getQuiver(qd);
            QuiverSet qs=new QuiverSet(qd);
            qs.add(new AbstractQuiver(q,qd));
            AbstractQuiver.addSinkSourceMutations(q,qs,qd);
            deleteQuiver();
            sinkSourceEqSet=qs;
        }
        return sinkSourceEqSet;
    }
    
    public void setQuiver(Quiver q){
	embeddedQuiver=q;
    }
    
    public void deleteQuiver(){
         embeddedQuiver=null;
    }
    
    public Quiver getQuiver(QuiverDrawing qd){
	if (embeddedQuiver==null){
	    //System.out.println("Embedded quiver is null.");
	    embeddedQuiver=new Quiver(this, qd);
            if (hist!=null){
                for (int i=0; i<hist.length; i++){
                    embeddedQuiver.Hist.add(hist[i]);
                }
                embeddedQuiver.Hist.historycounter=hist.length-1;
            }
        }
        //System.out.println("History of embedded quiver:"+embeddedQuiver.Hist.toString());
	return embeddedQuiver;
    }

    public AbstractQuiver toGraph(){
        if (underlyingGraph==null){
           
                BMatrix S=this.toMatrix();
                underlyingGraph=new AbstractQuiver(this, qd, GRAPH);
            
        }
        return underlyingGraph;
    }
    
    public void setcategory(int c){
	category=c;
    }
    
    public int getcategory(){
        return category;
    }

    
    public void sethistory(int length, Vector hi){
        //System.out.println("Length:"+length+" Vector:" + hi);
        hist=new int[length-1];
        for (int i=1; i<length;i++){
            hist[i-1]=perm[((Integer) hi.elementAt(i)).intValue()];
        }
    }
    
    public int gethistlength(){
        if (embeddedQuiver!=null){
            if (embeddedQuiver.Hist!=null){
            return embeddedQuiver.Hist.getlength();
            }
        }
        if (hist==null){return 0;}
        else {return hist.length;}
    }
    
    
    public String toString(){
        String s="";
        for (int i=0; i<Vertices.length; i++){
            if (i>0){s=s+"\n";}
            s=s+"Vertex "+i+"\n"+Vertices[i];
        }
        return s;
    }
    
    public BMatrix toMatrix(){
        int n=Vertices.length;
        BMatrix M=new BMatrix(n,n);
        for (int i=0; i<n;i++){
            for (int j=0; j<n; j++){
                M.A[i][j]=new BigInteger("0");
            }
        }
        for (int i=0;i<n;i++){
            Halfarrow[] h=Vertices[i].Starting;
            for (int j=0; j<h.length; j++){
                M.A[i][h[j].vertex]=h[j].v1;
                M.A[h[j].vertex][i]=h[j].v2;
            }
        }
        return M;
    }
    
    public BMatrix toMatrixWithPerm(){
        BMatrix B=toMatrix();
        if (perm!=null){B.permuteRowsCols(perm,perm);}
        return B;
    }

    public BigInteger getWeight(){return weight;}
    
    private static String permToString(int[] p){
        if (p==null){return "null";} 
        else if (p.length==0){return "Identity of empty set";}
        else {
          String s=""+p[0];
          for (int i=1; i<p.length; i++){
              s=s+" "+p[i];
          }
          return s;
        }
    }
    
    private static int[] invperm(int[] p){
        int[] q=new int[p.length];
        int i,j;
        //System.out.println("Direct: "+ permToString(p));
        for (i=0; i<p.length; i++){
            j=0;
            while ((j<p.length) && (p[j]!=i)){
                j++;
                }
            q[i]=j;
        }
        //System.out.println("Inverse: "+permToString(q));
        return q;
    }
    
    
   public static int[] searchIsom(Quiver Q1, Quiver Q2, QuiverDrawing qd){
        AbstractQuiver aq1=new AbstractQuiver(Q1,qd);
        AbstractQuiver aq2=new AbstractQuiver(Q2,qd);
        int[] isom=AbstractQuiver.seachIsom(aq1, aq2, qd);
        if (isom==null){
            return null;
        }
        int[] perm1=aq1.perm;
        int[] perm2=aq2.perm;
        //System.out.println("First quiver aq-isom.:"+Utils.toString(aq1.perm));
        //System.out.println("Second quiver aq-iso.:"+Utils.toString(aq2.perm));
        //System.out.println("Abstract Isomorphism : "+Utils.toString(isom));
        
        
        perm2=Utils.invperm(perm2);
        //System.out.println("Inverse of perm2: "+Utils.toString(perm2));
        //isom=Utils.invperm(isom);
        isom=Utils.compperm(perm2,isom);
        isom=Utils.compperm(isom,perm1);
        return isom;
   }
    
    public AbstractQuiver(Quiver argq, QuiverDrawing argqd){
       this(argq.M, argqd);
       for (int i=0; i<argq.nbpoints;i++){
           Vertices[perm[i]].frozen=argq.P[i].frozen;
       }
    }

    public AbstractQuiver(Quiver argq, QuiverDrawing argqd, boolean copyPoints){
       this(argq.M, argqd);
       for (int i=0; i<argq.nbpoints;i++){
           Vertices[perm[i]].frozen=argq.P[i].frozen;
       }
       embeddedQuiver=new Quiver(argq.M, qd);
       for (int i=0; i<argq.nbpoints; i++){
                        embeddedQuiver.P[i]=(MoveablePoint) argq.P[i].clone();
                    }
       if (argq.taumutseq!=null){
           embeddedQuiver.taumutseq=""+argq.taumutseq;}
       if (argq.tauperm!=null){
           embeddedQuiver.tauperm=""+argq.tauperm;}
       embeddedQuiver.tauorder=argq.tauorder;
       if ((argq.StyleMatrix!=null)&&(argq.nbpoints>0)){
            embeddedQuiver.StyleMatrix=new SMatrix(argq.StyleMatrix.M);
       }

       //System.out.println("Embedded quiver tauperm: "+embeddedQuiver.tauperm);
       //System.out.println("Embedded quiver taumutseq: "+embeddedQuiver.taumutseq);
    }
    
    public AbstractQuiver(BMatrix M, QuiverDrawing argqd){
       this(M,argqd, AbstractQuiver.QUIVER);
    }
    
    public AbstractQuiver(AbstractQuiver argaq, QuiverDrawing argqd, int type){
        this(argaq.toMatrix(), argqd, type);
        for (int i=0; i<argaq.Vertices.length;i++){
           Vertices[perm[i]].frozen=argaq.Vertices[i].frozen;
       }
    }
    
    
    public AbstractQuiver(BMatrix M, QuiverDrawing argqd, int type){
        ValencySequence[] vs;
        BMatrix T,V;
  
        
        underlyingGraph=null;
	embeddedQuiver=null;
        sinkSourceEqSet=null;
        qd=argqd;
	category=type;
	weight=M.weight();
        //hist=null;
        
        int n=M.nbrows;
        T=new BMatrix(n,n);
        T.copyfrom(M);
        //System.out.println(T);
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)<0){T.A[i][j]=null; T.A[i][j]=new BigInteger("0");}
            }
        }
        //System.out.println(T);
        if (type==GRAPH){
            //System.out.println(T);
            T.symmetrize();
            //System.out.println(T);
        }
        V=T.valencies();
        //System.out.println(V);
        
        vs=new ValencySequence[n];
        for (int i=0; i<n; i++){
            vs[i]=new ValencySequence(V.A[i], i);
            //System.out.println(vs[i]);
        }
        Arrays.sort(vs);
        /*
        for (int i=0; i<n; i++){
            System.out.println(vs[i]);
        }
         */
        perm=new int[vs.length];
        for (int i=0; i<vs.length;i++){
            perm[i]=vs[i].pos;
        }
        //T.permuteRowsCols(perm,perm);
        //System.out.println(T);
        
        T.copyfrom(M);
        T.permuteRowsCols(perm,perm);
        //System.out.println(T);
        
        int[] id=new int[n];
        for (int i=0; i<n; i++){id[i]=i;}
        V.permuteRowsCols(perm,id);
        
        Vertices=new Vertex[V.nbrows];
        
        perm=invperm(perm);
        
        switch (type){
        case QUIVER:
        for (int i=0; i<n; i++){
            int nbstart=0;
            int nbend=0;
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)>0){nbstart=nbstart+1;}
                if (T.A[j][i].compareTo(BigInteger.ZERO)>0){nbend=nbend+1;}
            }
            int startcter=0;
            int endcter=0;
            Vertices[i]=new Vertex(V.A[i],nbstart, nbend);
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)>0){
                    Vertices[i].Starting[startcter]=new Halfarrow(j, T.A[i][j], T.A[j][i]);
                    startcter++;
                }
                 if (T.A[i][j].compareTo(BigInteger.ZERO)<0){
                    Vertices[i].Ending[endcter]=new Halfarrow(j, T.A[i][j], T.A[j][i]);
                    endcter++;
                }
            }
        }
        break;
        case GRAPH:
        for (int i=0; i<n; i++){
            int nbstart=0;
            int nbend=0;
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)!=0){nbstart=nbstart+1;}
                if (T.A[j][i].compareTo(BigInteger.ZERO)!=0){nbend=nbend+1;}
            }
            int startcter=0;
            int endcter=0;
            Vertices[i]=new Vertex(V.A[i],nbstart, nbend);
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)!=0){
                    Vertices[i].Starting[startcter]=new Halfarrow(j, T.A[i][j].abs(), T.A[j][i].abs());
                    startcter++;
                    Vertices[i].Ending[endcter]=new Halfarrow(j, T.A[i][j].abs(), T.A[j][i].abs());
                    endcter++;
                }
            }
        }
        //System.out.println("Graph: "+this);
        break;
        }
        //System.out.println(this);
        //System.out.println(toMatrix());
        
    }
    
    
    
    
    
    public void updateFromEmbeddedQuiver(){
        ValencySequence[] vs;
        BMatrix T,V;
        
        if (embeddedQuiver==null){return;}
        
	weight=embeddedQuiver.weight();
        //hist=null;
        
        int n=embeddedQuiver.nbpoints;
        T=new BMatrix(n,n);
        T.copyfrom(embeddedQuiver.M);
        //System.out.println(T);
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)<0){T.A[i][j]=null; T.A[i][j]=BigInteger.ZERO;}
            }
        }
        //System.out.println(T);
        if (category==GRAPH){
            //System.out.println(T);
            T.symmetrize();
            //System.out.println(T);
        }
        V=T.valencies();
        //System.out.println(V);
        
        vs=new ValencySequence[n];
        for (int i=0; i<n; i++){
            vs[i]=new ValencySequence(V.A[i], i);
            //System.out.println(vs[i]);
        }
        Arrays.sort(vs);
        /*
        for (int i=0; i<n; i++){
            System.out.println(vs[i]);
        }
         */
        perm=new int[vs.length];
        for (int i=0; i<vs.length;i++){
            perm[i]=vs[i].pos;
        }
        //T.permuteRowsCols(perm,perm);
        //System.out.println(T);
        
        T.copyfrom(embeddedQuiver.M);
        T.permuteRowsCols(perm,perm);
        //System.out.println(T);
        
        int[] id=new int[n];
        for (int i=0; i<n; i++){id[i]=i;}
        V.permuteRowsCols(perm,id);
        
        Vertices=new Vertex[V.nbrows];
        
        perm=invperm(perm);
        
        switch (category){
        case QUIVER:
        for (int i=0; i<n; i++){
            int nbstart=0;
            int nbend=0;
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)>0){nbstart=nbstart+1;}
                if (T.A[j][i].compareTo(BigInteger.ZERO)>0){nbend=nbend+1;}
            }
            int startcter=0;
            int endcter=0;
            Vertices[i]=new Vertex(V.A[i],nbstart, nbend);
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)>0){
                    Vertices[i].Starting[startcter]=new Halfarrow(j, T.A[i][j], T.A[j][i]);
                    startcter++;
                }
                 if (T.A[i][j].compareTo(BigInteger.ZERO)<0){
                    Vertices[i].Ending[endcter]=new Halfarrow(j, T.A[i][j], T.A[j][i]);
                    endcter++;
                }
            }
        }
        break;
        case GRAPH:
        for (int i=0; i<n; i++){
            int nbstart=0;
            int nbend=0;
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)!=0){nbstart=nbstart+1;}
                if (T.A[j][i].compareTo(BigInteger.ZERO)!=0){nbend=nbend+1;}
            }
            int startcter=0;
            int endcter=0;
            Vertices[i]=new Vertex(V.A[i],nbstart, nbend);
            for (int j=0; j<n; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)!=0){
                    Vertices[i].Starting[startcter]=new Halfarrow(j, T.A[i][j].abs(), T.A[j][i].abs());
                    startcter++;
                    Vertices[i].Ending[endcter]=new Halfarrow(j, T.A[i][j].abs(), T.A[j][i].abs());
                    endcter++;
                }
            }
        }
        //System.out.println("Graph: "+this);
        break;
        }
        //System.out.println(this);
        //System.out.println(toMatrix());
        
    }
     
public static String intArrayToString(int[] a){
    if (a==null){return "null";}
    else if (a.length==0){return "Zero components";}
    else {
        String s=""+a[0];
        for (int i=1; i<a.length; i++){
            s=s+", "+a[i];
        }
        return s;
    }
}
    public boolean hasMultArrow(){
        boolean hma=false;
        for (int i=0; i<Vertices.length; i++){
            if (Vertices[i].hasMultArrowStarting()){hma=true;}
        }
        return hma;
    }
    
    public static void addSinkSourceMutations(Quiver q, QuiverSet qs, QuiverDrawing qd){
        int[] ss;
        AbstractQuiver aq;
        boolean isnew;
        ss=q.sinksSources();
        //System.out.println("AddSinkSourceMutations called with quiverset of size: "+qs.getnber());
        //System.out.println(intArrayToString(ss));
        for (int i=0; i<ss.length; i++){
            //qd.repaint();
            //JOptionPane.showMessageDialog(qd,"Mutating at " + ss[i]+ " of "+ AbstractQuiver.intArrayToString(ss));
            if (q.P[ss[i]].frozen){continue;}
            q.mutate(ss[i],1);
            aq=new AbstractQuiver(q,qd);
            isnew=qs.add(aq);
            //System.out.println("Is new:" + isnew);
            if (isnew){
                addSinkSourceMutations(q,qs, qd);
            }
            q.mutate(ss[i],1);
            //qd.repaint();
            //JOptionPane.showMessageDialog(qd,"After mutating back from " + ss[i] + " of " +AbstractQuiver.intArrayToString(ss));
        }
    }
    
    private static boolean checkpartialisom(AbstractQuiver q1, AbstractQuiver q2, int[] pe, int cter){
        boolean isom=true;
	Halfarrow ha1=null;
	Halfarrow ha2=null;
        int i=0;
        while (isom && (i<cter)){
	    /*
            if (q1.Vertices[i].nbarrowsto(cter)!=q2.Vertices[perm[i]].nbarrowsto(perm[cter])){
                isom=false;
            }
            if (q1.Vertices[cter].nbarrowsto(i)!=q2.Vertices[perm[cter]].nbarrowsto(perm[i])){
                isom=false;
            }
	    */
	    ha1=q1.Vertices[i].halfarrowto(cter);
	    ha2=q2.Vertices[pe[i]].halfarrowto(pe[cter]);
	    if (!Halfarrow.equalval(ha1,ha2)){isom=false;}
	    ha1=q1.Vertices[cter].halfarrowto(i);
	    ha2=q2.Vertices[pe[cter]].halfarrowto(pe[i]);
	    if (!Halfarrow.equalval(ha1,ha2)){isom=false;}
            i++;
            if (q1.Vertices[i].frozen!=q2.Vertices[pe[i]].frozen){isom=false;}
        }
        return isom;
    }
    
    private static int nextadmissible(int[] p, int cter, int startat, int stopat){
        int res=startat;
        boolean admissible=true;
        do {
            int j=0;
            admissible=true;
            while ((j<cter)&&(admissible)){
               if (res==p[j]){
                   admissible=false;
               }
               j++;
            }
            if (!admissible){res++;}
        } while ((!admissible) && (res<stopat));
        return res;
    }
    
     public static int[] seachIsom(AbstractQuiver argq1, AbstractQuiver argq2, QuiverDrawing qd){
        boolean equiv=true;
        boolean foundisom=false;
        int[] myperm=null;
        
        AbstractQuiver q1=null;
        AbstractQuiver q2=null;

	int category=Math.min(argq1.category,argq2.category);
        
        
        switch (category) {
            case GRAPH :
                q1=argq1.toGraph();
                q2=argq2.toGraph();
            break;
            default : 
                q1=argq1;
                q2=argq2;
                
        }
        
        
        if (q1.Vertices.length!=q2.Vertices.length){equiv=false;}
        else {
            int n=q1.Vertices.length;
            int i=0;
            while ((i<n) && equiv){
                if (!q1.Vertices[i].HasEqualValencies(q2.Vertices[i])){equiv=false;}
                i++;
            }
            if (equiv){
                
		/*
                String s="Valencies: ";
                for (int j=0; j<n; j++){
                    s=s+"\n"+j+":";
                    BigInteger[] Valencies=q1.Vertices[j].Valencies;
                    for (i=0; i<Valencies.length; i++){
                        s=s+" "+Valencies[i];
                    }
                }
                System.out.println(s);
		*/
                
                int[] startat=new int[n];
                int[] stopat=new int[n];
               
                int start=0;
                startat[0]=start;
                for (i=1; i<n; i++){
                    if (!q1.Vertices[i].HasEqualValencies(q1.Vertices[i-1])){
                       start=i;
                    }
                    startat[i]=start;
                }
                
                int stop=n;
                stopat[n-1]=n;
                for (i=n-2; i>=0; i--){
                    if (startat[i]!=startat[i+1]){
                        stop=i+1;
                    }
                    stopat[i]=stop;
                }
                
                /* s="";
                for (i=0; i<n; i++){s=s+" "+startat[i];}
                s=s+"\n";
                for (i=0; i<n; i++){s=s+" "+stopat[i];}
                System.out.println(s);
		*/
                
                
                
                int cter=0;
                boolean ispartialisom=true;
                myperm=new int[n];
                myperm[0]=0;
                
                while (!foundisom && (cter>=0)){
                    if (myperm[cter]<stopat[cter]){
                        
                        ispartialisom=checkpartialisom(q1,q2,myperm,cter);
                        if ((ispartialisom) && (cter==n-1)){
                            foundisom=true;
                            return myperm;
                        }
                        
                        /* System.out.println(cter+" : ");
                        s="";
                        for (int j=0; j<=cter; j++){
                          s=s+" "+perm[j];
                            }
                        System.out.println(s + " Partial isomorphism: "+ispartialisom);
			*/
                      }
                      else {
			  /*s="Silent: ";
                        for (int j=0; j<=cter; j++){
                          s=s+" "+perm[j];
                            }
			  */
                      }
			  

                      /*if ((cter==d-1) && isnew){
                          isexhaustive=false;
                      }*/

                      if ((cter<n-1) && (myperm[cter]<stopat[cter]) && ispartialisom){
                          cter++; 
                          myperm[cter]=nextadmissible(myperm,cter, startat[cter],stopat[cter]);
                          //q.mutate(0);
                      }
                      else if (myperm[cter]<stopat[cter]){
                          //q.mutate(i[cter]);
                          myperm[cter]=nextadmissible(myperm,cter,myperm[cter]+1, stopat[cter]);
                          if (myperm[cter]<stopat[cter]){
                              //q.mutate(i[cter]);
                          }
                      }
                      else {
                           cter=cter-1;
                           if (cter>=0){
                                  //q.mutate(i[cter]);
                                  myperm[cter]=nextadmissible(myperm,cter,myperm[cter]+1, stopat[cter]);
                                  if (myperm[cter]<stopat[cter]){
                                      //q.mutate(i[cter]);
                                  }
                              }
                          }
                      }
            }
            
        }
        
        return null;
        
    }
    
    public static boolean compare(AbstractQuiver argq1, AbstractQuiver argq2, QuiverDrawing qd){
        boolean equiv=true;
        
        boolean foundisom=false;
        
        AbstractQuiver q1=null;
        AbstractQuiver q2=null;

	int category=Math.min(argq1.category,argq2.category);
        
        
        switch (category) {
            case GRAPH :
                q1=argq1.toGraph();
                q2=argq2.toGraph();
            break;
            case SINK_SOURCE :
                q1=argq1;
                q2=argq2;
                return q1.getSinkSourceEqSet(qd).contains(q2);
                
            case QUIVER : 
                q1=argq1;
                q2=argq2;
              break;
            case QUIVER_MOD_ICE:
                q1=argq1.deleteFrozenNodes().toGraph();
                q2=argq2.deleteFrozenNodes().toGraph();
            break;
        }
                
        if (q1.Vertices.length!=q2.Vertices.length){equiv=false;}
        else {
            int n=q1.Vertices.length;
            int i=0;
            while ((i<n) && equiv){
                if (!q1.Vertices[i].HasEqualValencies(q2.Vertices[i])){equiv=false;}
                i++;
            }
            if (equiv){
                
		/*
                String s="Valencies: ";
                for (int j=0; j<n; j++){
                    s=s+"\n"+j+":";
                    BigInteger[] Valencies=q1.Vertices[j].Valencies;
                    for (i=0; i<Valencies.length; i++){
                        s=s+" "+Valencies[i];
                    }
                }
                System.out.println(s);
		*/
                
                int[] startat=new int[n];
                int[] stopat=new int[n];
               
                int start=0;
                startat[0]=start;
                for (i=1; i<n; i++){
                    if (!q1.Vertices[i].HasEqualValencies(q1.Vertices[i-1])){
                       start=i;
                    }
                    startat[i]=start;
                }
                
                int stop=n;
                stopat[n-1]=n;
                for (i=n-2; i>=0; i--){
                    if (startat[i]!=startat[i+1]){
                        stop=i+1;
                    }
                    stopat[i]=stop;
                }
                
                /* s="";
                for (i=0; i<n; i++){s=s+" "+startat[i];}
                s=s+"\n";
                for (i=0; i<n; i++){s=s+" "+stopat[i];}
                System.out.println(s);
		*/
                
                int[] perm=new int[n];
                
                int cter=0;
                boolean ispartialisom=true;
                perm[0]=0;
                
                while (!foundisom && (cter>=0)){
                    if (perm[cter]<stopat[cter]){
                        
                        ispartialisom=checkpartialisom(q1,q2,perm,cter);
                        if ((ispartialisom) && (cter==n-1)){
                            foundisom=true;
                        }
                        
                        /* System.out.println(cter+" : ");
                        s="";
                        for (int j=0; j<=cter; j++){
                          s=s+" "+perm[j];
                            }
                        System.out.println(s + " Partial isomorphism: "+ispartialisom);
			*/
                      }
                      else {
			  /*s="Silent: ";
                        for (int j=0; j<=cter; j++){
                          s=s+" "+perm[j];
                            }
			  */
                      }
			  

                      /*if ((cter==d-1) && isnew){
                          isexhaustive=false;
                      }*/

                      if ((cter<n-1) && (perm[cter]<stopat[cter]) && ispartialisom){
                          cter++; 
                          perm[cter]=nextadmissible(perm,cter, startat[cter],stopat[cter]);
                          //q.mutate(0);
                      }
                      else if (perm[cter]<stopat[cter]){
                          //q.mutate(i[cter]);
                          perm[cter]=nextadmissible(perm,cter,perm[cter]+1, stopat[cter]);
                          if (perm[cter]<stopat[cter]){
                              //q.mutate(i[cter]);
                          }
                      }
                      else {
                           cter=cter-1;
                           if (cter>=0){
                                  //q.mutate(i[cter]);
                                  perm[cter]=nextadmissible(perm,cter,perm[cter]+1, stopat[cter]);
                                  if (perm[cter]<stopat[cter]){
                                      //q.mutate(i[cter]);
                                  }
                              }
                          }
                      }
            }
            
        }
        return foundisom;
    }
    
    public boolean equals(Object o){
       //System.out.println("qd is null:"+ (qd==null));
       return compare(this, (AbstractQuiver) o, qd);
    }
}


class TraverseDialog extends JDialog implements ActionListener {
    JButton StartButton, StopButton, CloseButton;
    JRadioButton sinkSourceButton, graphButton, quiverButton;
    JTextField textField, depthField, multField, histField;
    JLabel presentDepthLabel, presentNumberLabel, freeMemoryLabel, statusLabel;
    JCheckBox gentleBox, compareBox, sinksourceBox;
    
    int stage; // 0 : nothing, 1: reduce qs, 2: sort qs;
    
    int type;
    boolean stopButtonPressed;
    QuiverSet qs;
    Quiver q;
    QuiverDrawing qd;
    Seed S;
    javax.swing.Timer timer;
    final int ONE_SECOND=1000;

    public long getFreeMemory(){
	Runtime runtime=Runtime.getRuntime();
	long maxMemory=runtime.maxMemory();
        long allocatedMemory=runtime.totalMemory();
        long freeMemory=runtime.freeMemory();
        return (freeMemory+(maxMemory-allocatedMemory))/1024;
    }


    public void actionPerformed(ActionEvent e) {
        String cmd=e.getActionCommand();
        //System.out.println(s);
        if (cmd==null){
            //System.out.println("If entered");
            setlabels(qs.getdepth(), qs.getnber(), getFreeMemory());
            if (stage==1){
                if (type!=AbstractQuiver.QUIVER){
                        qs.reduce(type);
                    }
                statusLabel.setText("Status: Sorting ...");
                stage=2;
                repaint();
            }
            else if (stage==2){
                timer.stop();
                System.out.println("Sorting according to weight order");
                QuiverSetSorter.sort(qs);
                setCursor(null); //turn off the wait cursor
		    StartButton.setEnabled(true);
		    StopButton.setEnabled(false);
		    CloseButton.setEnabled(true);
                    
                    sinkSourceButton.setEnabled(true);
		    graphButton.setEnabled(true);
		    quiverButton.setEnabled(true);
		    
                    presentNumberLabel.setText("Total number: "+qs.getnber());
                    statusLabel.setText("Status: Stopped");
                }
                else if (qs.done) {
                    if (type!=AbstractQuiver.QUIVER){
                       statusLabel.setText("Status: Eliminating ...");
                    }
                    stage=1;
                    repaint();
             }
        }
        if ("Stop".equals(cmd)) {
            stopButtonPressed=true;
            qs.stop();
            if (!compareBox.isSelected()){
            if (type!=AbstractQuiver.QUIVER){
                       statusLabel.setText("Status: Eliminating ...");
                    }
            }
            stage=1;
            repaint();
        }
	else if ("Start".equals(cmd)){
            String s;
            Integer MaxDepth=null;
            s=depthField.getText();
            MaxDepth=ParameterDialog.readInteger(s);
            qs.setMaxDepth(MaxDepth);
            
            Integer MaxMult=null;
            s=multField.getText();
            MaxMult=ParameterDialog.readInteger(s);
            qs.setMaxMult(MaxMult);
            
            qs.setOnlygentle(gentleBox.isSelected());
            if (compareBox.isSelected()){
                qs.setMemQuiver(S.getMemQuiver());
            }
            if (sinksourceBox.isSelected()){
                qs.setSearchsinksources(true);
            }
            
            
	    qs.clear();
            Quiver q1=new Quiver(q.M, qd);
            for (int i=0; i<q1.nbpoints; i++){
                q1.P[i].frozen=q.P[i].frozen;
            }
	    qs.setQuiver(q1);
	    
            qs.setcategory(AbstractQuiver.QUIVER);

            if (sinkSourceButton.isSelected())
		{type=AbstractQuiver.SINK_SOURCE;
            }
                 
	    if (graphButton.isSelected())
		{type=AbstractQuiver.GRAPH;
		    //System.out.println("Graph passed.");
		}
	    if (quiverButton.isSelected())
		{type=AbstractQuiver.QUIVER;
		    //System.out.println("Quiver passed");
		}
            //System.out.println("Type:" + type);
            //System.out.println("Qs category:"+ qs.getcategory());
	
  
	    StartButton.setEnabled(false);
	    StopButton.setEnabled(true);
	    CloseButton.setEnabled(false);
	    graphButton.setEnabled(false);
	    quiverButton.setEnabled(false);
            sinkSourceButton.setEnabled(false);
            statusLabel.setText("Status: Exploring ...");
            
            stage=0;

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    timer.start();
	    qs.go();
	}
	else if ("Close".equals(cmd)){
	    timer.stop();
	    setVisible(false);
            stopButtonPressed=true;
            qs.stop();
            qs.setTracker(null);

            if (!compareBox.isSelected()){
                if (qs.getnber()>0){
                    qd.S.Q=qs.first().getQuiver(qd);
                }
                qd.repaint();
                int initialQuiver=qs.getindex(new AbstractQuiver(q.M, qd));
                if (initialQuiver!=-1){
                    //System.out.println("Original quiver found at: "+initialQuiver);
                    qs.elementAt(initialQuiver).setQuiver(q);
                    S.Q=qs.first().getQuiver(qd);
                    qd.repaint();
                    }
            }
            else {
                Quiver memQuiver=S.getMemQuiver();
                int memquiverindex=qs.getindex(new AbstractQuiver(memQuiver, qd));
                if (memquiverindex!=-1){
                    JOptionPane.showMessageDialog(qd,"The quiver in memory was found.");
                    //System.out.println("Original quiver found at: "+initialQuiver);
                    S.Q=qs.getelementAt(memquiverindex).getQuiver(qd);
                    qs.clear();
                    qd.repaint();
                    }
                else {
                     JOptionPane.showMessageDialog(qd,"The quiver in memory was not found.");
                }
            }
	    qd.updatetoolmenu();
            qd.updatestatus(qd.status);
	}
    }
    
    public void setlabels(int d, int f, long m){
	presentDepthLabel.setText("Present depth: " + d);
	presentNumberLabel.setText("Found so far: " + f);
	freeMemoryLabel.setText("Free memory: " + m+ " K");
    }
    
    public TraverseDialog(Frame fr, QuiverDrawing argqd, QuiverSet argqs, 
			  Seed argS){

	super(fr, "Computing the mutation class", false);

	qd=argqd;
	qs=argqs;
	S=argS;
	q=S.Q;
	

	stopButtonPressed=false;
        stage=0;

        StartButton = new JButton("Start");
        StartButton.addActionListener(this);
        StopButton = new JButton("Stop");
	StopButton.setEnabled(false);
        StopButton.addActionListener(this);
        CloseButton = new JButton("Close");
        CloseButton.addActionListener(this);
        
        getRootPane().setDefaultButton(StartButton);

	JLabel relationLabel=new JLabel("Equivalence relation: ");

        
	graphButton=new JRadioButton("Valued graph isomorphism");
	graphButton.setSelected(true);
	quiverButton=new JRadioButton("Valued quiver isomorphism");
	ButtonGroup group = new ButtonGroup();
	quiverButton.setSelected(false);
        sinkSourceButton=new JRadioButton("Sink/source equivalence");
	sinkSourceButton.setSelected(false);
        
        group.add(graphButton);
        group.add(quiverButton);
        group.add(sinkSourceButton);

        sinkSourceButton.addActionListener(this);
	graphButton.addActionListener(this);
        quiverButton.addActionListener(this);

	textField = new JTextField(5);
	textField.setText("500");
        textField.setActionCommand("textField");
        textField.addActionListener(this);
	JLabel textFieldLabel = new JLabel("Expected size: ");
	textFieldLabel.setLabelFor(textField);
        
        depthField=new JTextField(5);
        depthField.setActionCommand("depthField");
        depthField.addActionListener(this);
        JLabel depthFieldLabel=new JLabel("Maximal depth: ");
        depthFieldLabel.setLabelFor(depthField);
        gentleBox=new JCheckBox("only gentle mutations");
        compareBox=new JCheckBox("compare with quiver in memory");
        sinksourceBox=new JCheckBox("look for sink/source");
        
        
        multField=new JTextField(5);
        multField.setActionCommand("multField");
        multField.addActionListener(this);
        JLabel multFieldLabel=new JLabel("Maximal mult.: ");
        multFieldLabel.setLabelFor(multField);
        
        histField=new JTextField(5);
        histField.setActionCommand("histField");
        multField.addActionListener(this);
        JLabel histFieldLabel=new JLabel("Max. history.: ");
        histFieldLabel.setLabelFor(histField);
        
        JLabel fillLabel=new JLabel("");
        
        
	JPanel textPanel=new JPanel(new GridLayout(7,1,0,5));
        //JPanel textPanel=new JPanel();
        //textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
	
        //textPanel.add(textFieldLabel);
	//textPanel.add(textField);
        textPanel.add(depthFieldLabel);
        textPanel.add(depthField);
        textPanel.add(multFieldLabel);
        textPanel.add(multField);
        textPanel.add(gentleBox);
        textPanel.add(compareBox);
        textPanel.add(sinksourceBox);
        //textPanel.add(histFieldLabel);
        //textPanel.add(histField);
        textPanel.setBorder(BorderFactory.createEmptyBorder(35,10,10,10));

	JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	radioPanel.add(relationLabel);
        radioPanel.add(graphButton);
        radioPanel.add(quiverButton);
        radioPanel.add(sinkSourceButton);
        radioPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
                
        Box statusPanel = new Box(BoxLayout.Y_AXIS);
        presentDepthLabel=new JLabel("Present depth:             ");
        presentNumberLabel=new JLabel("Found so far:             ");
	freeMemoryLabel=   new JLabel("Free memory: " + getFreeMemory()+" K");
        statusLabel= new JLabel("Status: Stopped");
        
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(presentDepthLabel);
        statusPanel.add(presentNumberLabel);
	statusPanel.add(freeMemoryLabel);
        statusPanel.add(statusLabel);
        
        //getContentPane().add(statusPanel, BorderLayout.SOUTH);
        
        //Lay out the buttons from left to right.
        JPanel buttonPanel = new JPanel();
        //buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        buttonPanel.add(StartButton);
	buttonPanel.add(StopButton);
	buttonPanel.add(CloseButton);
        
        //JPanel topPane=new JPanel(new GridLayout(1,0));
        //topPane.add(radioPanel);
        //topPane.add(textPanel);
        
        JPanel firstTab=new JPanel();
        firstTab.setLayout(new BoxLayout(firstTab, BoxLayout.PAGE_AXIS));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstTab.add(radioPanel);
        firstTab.add(statusPanel);
        firstTab.add(buttonPanel);
        
        //JPanel bottomPane=new JPanel(new GridLayout(1,0));
        //bottomPane.add(statusPanel);
        //bottomPane.add(buttonPanel);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Basic",firstTab);
        tabbedPane.addTab("Advanced", textPanel);
        
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        //contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
	//contentPane.add(topPane);
        //contentPane.add(bottomPane);
        
        contentPane.add(tabbedPane);
        
        //Initialize values.
        pack();
        setLocationRelativeTo(qd);
	setVisible(true);

        timer = new javax.swing.Timer(ONE_SECOND/2, this);
                
                /*new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
		setlabels(qs.getdepth(), qs.getnber(), getFreeMemory());
                if (qs.done) {
                    timer.stop();
                    setCursor(null); //turn off the wait cursor
                }
            }
	    });*/
        //timer.start();
	//qs.go();
    }
}


    
    class ParameterDialog extends JDialog implements ActionListener {
        
    public static final int LENGTH_DEFAULT=75;
    public static final int LAPSE_DEFAULT=100;
    
    JButton CloseButton, ApplyButton, cancelButton;
    JTextField lengthField, lapseField;
    JCheckBox ShakeBox;
    
    QuiverDrawing qd;

    JLabel lengthLabel, lapseLabel;
    
    boolean cancelButtonPressed;
    
    public static Integer readInteger(String s){
        Integer res=null;
         try {
              res=new Integer(Integer.parseInt(s));
            } catch (NumberFormatException ex){
                res=null;
            }
        return res;
    }
    
    public static int readint(Component comp, String s, int lower, int upper, int defaultvalue){
        int res;
        try {
              res=Integer.parseInt(s);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(comp, "Enter a number between "+lower+" and "+upper+".");
                res=defaultvalue;
            }
        if ((res<lower)|| (res>upper)){
            JOptionPane.showMessageDialog(comp, "Enter a number between "+lower+" and "+upper+".");
            res=defaultvalue;
        }
        return res;
    }

    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        //System.out.println(s);
        if ("Cancel".equals(s)) {
            setVisible(false);
        }
	else if (("Close".equals(s))|| ("Apply".equals(s)) || ("lapseField".equals(s)) || ("lengthField".equals(s))||("ShakeBox".equals(s))){
            int edgelen=readint(this, lengthField.getText(), 5, 1000, LENGTH_DEFAULT);
            qd.setedgeLength(edgelen);
            int timelap=readint(this, lapseField.getText(), 1,10000, LAPSE_DEFAULT);
            qd.settimeLapse(timelap);
            lengthField.setText(""+edgelen);
            lapseField.setText(""+timelap);
            if ("ShakeBox".equals(s)){
                if (ShakeBox.isSelected()){
                    qd.replayTimer=new javax.swing.Timer(1000,qd);
                    qd.replayTimer.start();
                    //System.out.println("ShakeTimer set.");
                }
                else {
                    qd.replayTimer.stop();
                    qd.replayTimer=null;
                    //System.out.println("ShakeTimer off.");
                }
            }
            if ("Close".equals(s)){
	    setVisible(false);
            }
        }
    }
            
    

    public ParameterDialog(Frame fr, QuiverDrawing qd){

	super(fr, "Parameters for live quivers", false);
        
        this.qd=qd;

	cancelButtonPressed=false;

        CloseButton=new JButton("Close");
        CloseButton.addActionListener(this);
        ApplyButton=new JButton("Apply");
        ApplyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        
        getRootPane().setDefaultButton(ApplyButton);
        
	lengthField = new JTextField(5);
	lengthField.setText(""+qd.edgeLength);
        lengthField.setActionCommand("lengthField");
        lengthField.addActionListener(this);
        
        lapseField = new JTextField(5);
	lapseField.setText(""+qd.timeLapse);
        lapseField.setActionCommand("lapseField");
        lapseField.addActionListener(this);
        
        JLabel lengthLabel=new JLabel("Arrow length in points (5-1000): ");
        JLabel lapseLabel=new JLabel("Lapse in milliseconds: (1-10000): ");
        
	lengthLabel.setLabelFor(lengthField);
        lapseLabel.setLabelFor(lapseField);
        
        ShakeBox=new JCheckBox("Random shakes");
        ShakeBox.setActionCommand("ShakeBox");
        ShakeBox.addActionListener(this);
        
        
	JPanel textPanel=new JPanel(new GridLayout(0,1));
	textPanel.add(lengthLabel);
	textPanel.add(lengthField);
        textPanel.add(lapseLabel);
	textPanel.add(lapseField);
        //textPanel.add(ShakeBox);
        
        textPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        
        getContentPane().add(textPanel, BorderLayout.CENTER);
        
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        //buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(cancelButton);
        buttonPane.add(ApplyButton);
        buttonPane.add(CloseButton);
	
        
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(textPanel, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(qd);
	setVisible(true);
    }
}

    
class SequencesDialog extends JDialog implements ActionListener {
        
    public static final int SEQUENCE_NUMBER=10;
    
    JButton closeButton;
    JTextField[] nameField, sequenceField, permField;
    
    QuiverDrawing qd;
   
    
    public void write(BufferedWriter out){
        try{ 
            out.write("//Sequences dialog"); out.newLine();
	    out.write("//Number of sequences"); out.newLine();
	    out.write(""+SEQUENCE_NUMBER); out.newLine();
            for (int i=0;i<SEQUENCE_NUMBER; i++){
                out.write(nameField[i].getText()); out.newLine();
                out.write(sequenceField[i].getText()+","+permField[i].getText()); out.newLine();
            }
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
    
    public void read(BufferedReader in){
        String str;
        try{
            str=in.readLine();
            //System.out.println("First string in read sequences: "+ str);
	    str=in.readLine();
            //System.out.println("Second string in read sequences: "+str);
	    int nbseq=Integer.parseInt(str);
            for (int i=0; i<nbseq; i++){
                str=in.readLine();
                //System.out.println("String1 "+ i + " : "+str);
                if (i<SEQUENCE_NUMBER){nameField[i].setText(str);}
                str=in.readLine();
                //System.out.println("String2 "+ i + " : "+str);

                if (i<SEQUENCE_NUMBER){
                    if (str.indexOf(",")>-1){
                        String[] fields=str.split(",");
                        //System.out.println("i="+i+ " str="+str);
                        //System.out.println("fields[0]="+fields[0]+" fields[1]="+fields[1]);
                        if (fields.length>0){sequenceField[i].setText(fields[0]);}
                        if (fields.length>1){permField[i].setText(fields[1]);}
                    }
                    else {
                        sequenceField[i].setText(str);
                    }
                }
            }
        }
        catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
    
    
    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        boolean shiftdown=((e.getModifiers() & ActionEvent.SHIFT_MASK)==ActionEvent.SHIFT_MASK);
        //System.out.println(s);
        if ("Close".equals(s)) {
            setVisible(false);
        }
        
        for (int k=0; k<SEQUENCE_NUMBER; k++){
            String str=sequenceField[k].getText();
            int[] seq=null;
            if (str!=null){
               seq=Utils.StringToIntArray(str);
            }

            str=permField[k].getText();
            int[] perm=null;
            if (str!=null){
               perm=Utils.StringToIntArray(permField[k].getText());
            }

            if (((k+1)+"+").equals(s)){
                qd.S.mutate(seq,perm);
                qd.repaint();
            }
            
            if (((k+1)+"-").equals(s)){
                qd.S.invmutate(seq,perm);
                qd.repaint();
            }

            if (((k+1)+"o").equals(s)){
                int bound=1000;
                int order=qd.S.mutorder(seq, perm, bound);
                if (order>-1){
                    JOptionPane.showMessageDialog(this,"Order="+order);
                    }
                else {
                   JOptionPane.showMessageDialog(this,"Order > "+bound);
                }
            }
             if (("M"+(k+1)).equals(s)){
                 qd.S.marknodes(seq);
            }
        }
    }
          
    public void setNames(String names[]){
        for (int k=0; k<names.length; k++){
            nameField[k].setText(names[k]);
        }
    }
    
    public void setName(String argname){
        nameField[0].setText(argname);
    }
    
    public void setSequences(String sequences[]){
        for (int k=0; k<sequences.length; k++){
            sequenceField[k].setText(sequences[k]);
        }
    }
    
    public void setSequence(String argseq){
        sequenceField[0].setText(argseq);
    }
    
    public void setPerm(String argperm){
        permField[0].setText(argperm);
    }

     public SequencesDialog(QuiverDrawing qd){
        this(qd,true);
    }

    public SequencesDialog(QuiverDrawing qd, boolean isvisible){

	super(qd.fr, "Sequences of mutations", false);

        this.qd=qd;


        closeButton=new JButton("Close");
        closeButton.addActionListener(this);
        JPanel closeButtonPane=new JPanel();
        closeButtonPane.setLayout(new BoxLayout(closeButtonPane, BoxLayout.LINE_AXIS));
        closeButtonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        closeButtonPane.add(Box.createHorizontalGlue());
        closeButtonPane.add(closeButton);
        
        getRootPane().setDefaultButton(closeButton);
        
        String s="Enter vertex sequences (separated by spaces) in the first column.\n";
        s=s+"Enter value tables of permutations in the second column.\n";
        s=s+"Optional: Enter names in the third column.\n";
        s=s+"Click the buttons to execute the mutation sequences, respectively their inverses.";
        JTextArea explanationArea=new JTextArea(3, 40);
        explanationArea.setText(s);
        explanationArea.setEditable(false);
        explanationArea.setLineWrap(true);
        JPanel labelPane=new JPanel();
        labelPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        labelPane.add(explanationArea);
        
        JPanel panel=new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(labelPane);
        
        nameField=new JTextField[SEQUENCE_NUMBER];
        sequenceField=new JTextField[SEQUENCE_NUMBER];
        permField=new JTextField[SEQUENCE_NUMBER];
        Dimension size=new Dimension(45,22);
        //ImageIcon icon = createAppletImageIcon("icons/plusIcon.png","a plus sign");
        

        for (int k=0; k<SEQUENCE_NUMBER;k++){
            
            nameField[k] = new JTextField(3);
            sequenceField[k]= new JTextField(10);
            permField[k]= new JTextField(10);
        
            JButton plusButton=new JButton("+");
            plusButton.setActionCommand((k+1)+"+");
            plusButton.setPreferredSize(size);
            plusButton.addActionListener(this);
            JButton minusButton=new JButton("-");
            minusButton.setActionCommand((k+1)+"-");
            minusButton.setPreferredSize(size);
            minusButton.addActionListener(this);
            
            JButton orderButton=new JButton("o");
            orderButton.setActionCommand((k+1)+"o");
            orderButton.setPreferredSize(size);
            orderButton.addActionListener(this);
            
            JButton markButton=new JButton("M");
            markButton.setActionCommand("M"+(k+1));
            markButton.setPreferredSize(size);
            markButton.addActionListener(this);

            JPanel actionButtonPane = new JPanel();
            //actionButtonPane.setLayout(new BoxLayout(actionButtonPane, BoxLayout.LINE_AXIS));
            //actionButtonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            actionButtonPane.add(plusButton);
            actionButtonPane.add(minusButton);
            actionButtonPane.add(orderButton);
            actionButtonPane.add(markButton);
            
            JPanel rowPane=new JPanel();
            //rowPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            rowPane.add(sequenceField[k]);
            rowPane.add(permField[k]);
            rowPane.add(nameField[k]);
            rowPane.add(actionButtonPane);
            
            panel.add(rowPane);
        }
       
        panel.add(closeButtonPane);
        //panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        getContentPane().add(panel);

        pack();
        setLocationRelativeTo(qd);
	setVisible(isvisible);
    }


}

class QuiverSetSorter {
    static final Comparator HIST_LENGTH_ORDER =
                                 new Comparator() {
        public int  compare(Object aq1, Object aq2) {
            //System.out.println("History length comparison");
            int l1=((AbstractQuiver)aq1).gethistlength();
            int l2=((AbstractQuiver)aq2).gethistlength();
            if (l1<l2){return 1;} else {
                int res=((AbstractQuiver)aq1).getWeight().compareTo(((AbstractQuiver)aq2).getWeight());
                if (l1==l2){
                    return res;}
                else {
                    return -1;
                }
            }
        }
    };
    static final Comparator WEIGHT_ORDER =
                                 new Comparator() {
        public int compare(Object aq1, Object aq2) {
            //System.out.println("Weight order");
	    return ((AbstractQuiver)aq1).getWeight().compareTo(((AbstractQuiver)aq2).getWeight());
        }
    };
    static final Comparator DOUBLE_WEIGHT_ORDER =
                                 new Comparator() {
        public int compare(Object aq1, Object aq2) {
            int res=((AbstractQuiver)aq1).getWeight().compareTo(((AbstractQuiver)aq2).getWeight());
            boolean m1=((AbstractQuiver)aq1).hasMultArrow();
            boolean m2=((AbstractQuiver)aq2).hasMultArrow();
            if (m1==m2){return res;}
            if (m2){return 1;} else {return -1;}
        }
     };
     static final Comparator TAU_ORDER_ORDER =
                             new Comparator(){
         public int compare(Object aq1, Object aq2){
             Quiver q1=((AbstractQuiver)aq1).getQuiver(null);
             Quiver q2=((AbstractQuiver)aq2).getQuiver(null);
             int res=0;
             int r1=q1.tauorder;
             int r2=q2.tauorder;
             if (r1<r2){
                res=1;
             }
             else if (r1==r2) {
                 if (q1.nbpoints<q2.nbpoints){
                     res=1;
                 }
                 else if (q1.nbpoints==q2.nbpoints){
                     res=0;
                }
             }
            else {
                 res=-1;
             }
            //System.out.println("q1= "+r1+" q2= "+ r2+ " res="+res);
             //res=-res;
            return res;
      }
    };
     
    public QuiverSetSorter(){}

       public static void sort(QuiverSet qs){
	   Collections.sort(qs.v, WEIGHT_ORDER);
    }
    
       public static void sort(QuiverSet qs, int order){
        switch (order){
            case 0: 
                System.out.println("Comparing quivers in weight-order");
                Collections.sort(qs.v, WEIGHT_ORDER);
                break;
            case 1: 
                System.out.println("Comparing quivers in double weight order");
                Collections.sort(qs.v, DOUBLE_WEIGHT_ORDER);
                break;
            case 2:
                System.out.println("Comparing quivers in tau-order");
                Collections.sort(qs.v, TAU_ORDER_ORDER);
                break;
            case 3:
                System.out.println("Comparing quivers in hist. length order");
                Collections.sort(qs.v, HIST_LENGTH_ORDER); 
                break;
        } 
    }
}



class QuiverSet{
    Vector v;
    int cter;
    int depth;
    int foundsofar;
    boolean canceled;
    boolean done;
    Quiver q;
    QuiverDrawing qd;
    int category;
    Integer MaxDepth;
    Integer MaxMult;
    Integer MaxHist;
    boolean onlygentle;
    boolean searchsinksources;
    AbstractQuiver absquiverWithSinkSource;
    Quiver memQuiver;
    
    Tracker myTracker;

    
    public void setTracker(Tracker tr){
        myTracker=tr;
    }
    
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                canceled = false;
		done=false;
                return traverseHoriz(qd);
            }
        };
        worker.start();
    }



    class IntVector extends Vector{
        
        public IntVector(int size){
            super(size);
        }
        
        public int el(int i){
            return ((Integer) elementAt(i)).intValue();
        }

	public void addint(int i){
	    add(new Integer(i));
	}
 
	public void put(int i, int n){
	    if (i<size()){
		set(i, new Integer(n));}
	    else if (i==size()){
		add(new Integer(n));
		    }
	    else {
		System.out.println("addint: size(): "+size() + " i: "+ 
				   i + " n: "+n);
	    }
	}

	public void increase(int i){
	    int m=((Integer) elementAt(i)).intValue();
	    m++;
	    set(i, new Integer(m));
	}
    }

    public void setQuiver(Quiver argq){
	q=argq;
    }
    
    public QuiverSet(QuiverDrawing argqd){
        v=new Vector(100);
        cter=0;
	depth=0;
        MaxDepth=null;
        MaxMult=null;
        MaxHist=null;
	canceled=false;
	category=AbstractQuiver.QUIVER;
        qd=argqd;
        myTracker=null;
        onlygentle=false;
        searchsinksources=false;
        memQuiver=null;
    }

    public void clear(){
	v.clear();
        cter=0;
	depth=0;
	canceled=false;
	category=AbstractQuiver.QUIVER;
    }

    public void setMemQuiver(Quiver argq){memQuiver=argq;}
    public void setOnlygentle(boolean og){onlygentle=og;}
    public void setSearchsinksources(boolean s){searchsinksources=s;}
    public void setMaxDepth(Integer md){MaxDepth=md;}
    public void setMaxMult(Integer mm){MaxMult=mm;}
    public void setMaxHist(Integer mh){MaxHist=mh;}
    public int getdepth(){return cter;}
    public void setdepth(int d){depth=d;}

    public void setcategory(int c){category=c;}
    public int getcategory(){return category;}

    public int getnber(){return v.size();}
    
    public int getcter(){return cter;}
    public void setcter(int c){cter=c;}

    public void stop(){canceled=true; }

    public void append(AbstractQuiver aq){
	v.add(aq);
    }
    
    public boolean contains(AbstractQuiver aq){
        // determines if a quiver isomorphic to aq in the category of valued quivers is contained in this.
        boolean isnew=true;
        int aqcat=aq.getcategory();
        aq.setcategory(AbstractQuiver.QUIVER);
        int n=v.size();
        int i=0;
        while (isnew && i<n){
            AbstractQuiver aq1=(AbstractQuiver) v.elementAt(i);
            int aq1cat=aq1.getcategory();
            aq1.setcategory(AbstractQuiver.QUIVER);
            if (aq1.equals(aq)){isnew=false;}
            aq1.setcategory(aq1cat);
            i++;
        }
        aq.setcategory(aqcat);
        //System.out.println("contains: i="+i+" isnew: "+isnew);
        return !isnew;
    }
    
    public int nbQuivWithMultArrows(){
        int res=0;
        for (int i=0; i<v.size(); i++){
            if (((AbstractQuiver) v.elementAt(i)).hasMultArrow()){
                res++;
            }
        }
        return res;
    }
    
    public int nbTaufiniteQuivers(){
        Quiver q;
        int res=0;
        for (int i=0; i<v.size(); i++){
            q=  ((AbstractQuiver) v.elementAt(i)).getQuiver(qd);
            if  (q.tauorder!=-1){
                res++;
            }
        }
        return res;
    }
    
    public boolean add(AbstractQuiver aq){
        // adds aq if aq is not isomorphic in this.category to an element of this.
        boolean isnew=true;
        int aqcat=aq.getcategory();
        aq.setcategory(category);
        int n=v.size();
        int i=0;
        while (isnew && i<n){
            AbstractQuiver aq1=(AbstractQuiver) v.elementAt(i);
            int aq1cat=aq1.getcategory();
            aq1.setcategory(category);
            if (aq1.equals(aq)){isnew=false;}
            aq1.setcategory(aq1cat);
            i++;
        }
        aq.setcategory(aqcat);
        if (isnew){v.add(aq);}
        return isnew;
    }
    
    public void delta(int type){
        int i=0;
        boolean found=false;
        while (i<v.size()){
            AbstractQuiver aq=elementAt(i);
            aq.setcategory(type);
            found=false;
            int j=i+1;
            while (j<v.size()){
                AbstractQuiver aq1=elementAt(j);
                aq1.setcategory(type);
                if (aq.equals(aq1)){
                    v.remove(j);
                    found=true;
                    System.out.println("Quiver "+i+" equals "+j);
                }
                else {
                    System.out.println("Quiver "+i+" unequals "+j);
                    j++;
                }
            }
            if (!found){
               i++;
            }
        }
    }
    
    public void reduce(int type){
        int i=0;
        while (i<v.size()){
            AbstractQuiver aq=elementAt(i);
            aq.setcategory(type);
            int j=i+1;
            while (j<v.size()){
                AbstractQuiver aq1=elementAt(j);
                aq1.setcategory(type);
                if (aq.equals(aq1)){
                    v.remove(j);
                }
                else {
                    j++;
                }
            }
            i++;
        }
       
    }
    
    public AbstractQuiver elementAt(int i){
	//cter=i;
        return (AbstractQuiver) v.elementAt(i);
    }

    public int getindex(AbstractQuiver aq){
	boolean found=false;
        int i=-1;
        if (v.size()>0){
            do { 
                i++;
                if (v.elementAt(i).equals(aq)){found=true;}
            } while ((i<v.size()-1) &&(!found));
        }
	if (found){ return i;} else {return -1;}
    }
    
    
    public void insertLinkToQuiver(Quiver q){
        int index=getindex(new AbstractQuiver(q.M, qd));
	    if (index!=-1){
		//System.out.println("Original quiver found at: "+initialQuiver);
		elementAt(index).setQuiver(q);
	        }
    }
    
    public Quiver getSinkSourceQuiver(){
        if (absquiverWithSinkSource!=null){
            System.out.println("History of sink/source quiver: "+Utils.toString(absquiverWithSinkSource.hist));
            System.out.println("Permutation: "+Utils.toString(absquiverWithSinkSource.perm));
            return absquiverWithSinkSource.getQuiver(qd);
        }
        else {
            return null;
        }
    }
        
    public int size(){
        return v.size();
    }

    public AbstractQuiver first(){
	cter=0;
	//System.out.println("Cter:"+cter+ " v.size()="+v.size());
	return (AbstractQuiver) v.elementAt(cter);
    }
    
    public void updateCurrentFromEmbeddedQuiver(){
        ((AbstractQuiver) v.elementAt(cter)).updateFromEmbeddedQuiver();
    }
    
    public AbstractQuiver next(){
        cter++;
	while (cter<0){cter=cter+v.size();}
	while (cter>=v.size()){cter=cter-v.size();}
	//System.out.println("Cter:"+cter+ " v.size()="+v.size());
        AbstractQuiver aq=(AbstractQuiver) v.elementAt(cter);
        return aq;
    }
    
    public AbstractQuiver getCurrent(){
        AbstractQuiver aq=(AbstractQuiver) v.elementAt(cter);
        return aq;
    }
    
    public void removePreceding(){
        cter--;
        while (cter<0){cter=cter+v.size();}
        v.remove(cter);
    }

    public AbstractQuiver getelementAt(int i){
        AbstractQuiver aq=(AbstractQuiver) v.elementAt(i);
        return aq;
    }

    public AbstractQuiver previous(){
        cter--;
	while (cter<0){cter=cter+v.size();}
	while (cter>=v.size()){cter=cter-v.size();}
	//System.out.println("Cter:"+cter+ " v.size()="+v.size());
        AbstractQuiver aq=(AbstractQuiver) v.elementAt(cter);
        return aq;
    }

public void write(BufferedWriter out, QuiverDrawing qd){
	try{ 
	    out.write("//Number of abstract quivers"); out.newLine();
	    out.write(""+v.size()); out.newLine();
	    out.write("//Counter"); out.newLine();
	    out.write(""+cter); out.newLine();
            out.write("//Category"); out.newLine();
	    out.write(""+category); out.newLine();
	    out.write("//Quivers"); out.newLine();
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
        for (int i=0; i<v.size(); i++){
            ((AbstractQuiver)v.elementAt(i)).getQuiver(qd).write(out);
        }
    }

public void read(BufferedReader in, QuiverDrawing qd){
	String str;
        int nberAbsQuiv=0;
	try{
	    if (qd.lastReadLine!=null){
                str=qd.lastReadLine;
            }
            else
            {
                str=in.readLine();
            }
            //System.out.println(str);
            if ("//Number of abstract quivers".equals(str)){
	    str=in.readLine();
	    //System.out.println(str);
            
	    nberAbsQuiv=Integer.parseInt(str);
	    in.readLine();
	    str=in.readLine();
            cter=Integer.parseInt(str);
            //System.out.println(str);
            
            in.readLine();
	    str=in.readLine();
            category=Integer.parseInt(str);
            //System.out.println(str);
            
            in.readLine();
            }
            else {
                nberAbsQuiv=0;
            }
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
        Quiver q=null;
        AbstractQuiver aq=null;
        for (int i=0; i<nberAbsQuiv; i++){
            q=new Quiver(Quiver.GLSAN, 1, qd);
            q.read(in);
            aq=new AbstractQuiver(q.M, qd);
            aq.setQuiver(q);
            v.add(aq);
        }
        qd.updatetoolmenu();
    }
    
    public Boolean traverse(QuiverDrawing qd){
	
       done=false;
       canceled=false;
       boolean isexhaustive=true;
       
       
       AbstractQuiver aq=new AbstractQuiver(q.M,qd);
       //System.out.println(q.M);
       
       aq.setcategory(category);
       boolean isnew=true;
       //boolean isnew=add(aq);
       //System.out.println("\nIs new:"+isnew + " "+ aq);
        
      cter=0;
     
      foundsofar=0;
      int n=aq.Vertices.length;
      //int[] i=new int[d];
      IntVector iv=new IntVector(depth);
      iv.addint(-1);
      //q.mutate(0);
      
      //System.out.println("qd is null :"+ (qd==null));
      
      while (!canceled & (cter>=0)){
          
          
          if (iv.el(cter)<n){
              
              isnew=true;
              if (cter>0){
                  if (iv.el(cter)==iv.el(cter-1)){
                      isnew=false;
                  }
              }
              if (isnew){
                  if (MaxDepth!=null){
                      if (cter>MaxDepth.intValue()){
                          isnew=false;
                      }
                  }
                  if (MaxMult!=null){
                      if (q.M.maxMultExceeds(MaxMult).booleanValue()){
                          isnew=false;
                      }
                  }
              }
              if (isnew){
                  aq=new AbstractQuiver(q.M,qd);
                  isnew=add(aq);
                  //qd.S.Q=q; qd.repaint();
                  //JOptionPane.showMessageDialog(qd, "Is new: " + isnew);
                  
                  if (isnew){
                      aq.setcategory(category);
                      if (MaxHist!=null){
                      if (cter<MaxHist.intValue()){
                          aq.sethistory(cter+1, iv);
                      }
                      }
                  }
              }
              
              /*
              String s="";
              for (int j=0; j<=cter; j++){
                    s=s+" "+iv.el(j);
                    }
              System.out.println("Sequence: "+s+" new: "+isnew);
              
              foundsofar=v.size();
               */
              
	      
              /*if (isnew){
                  System.out.println("Cter="+cter+" iv.size()=" +iv.size());
                  
                  System.out.println(aq.toMatrix());
                  }
               */
	    //System.out.println("Found so far: "+foundsofar);
            //System.out.println(cter+" : " +iv.size());
          }
          else {
              String s="Silent: ";
              for (int j=0; j<=cter; j++){
		s=s+" "+iv.el(j);
                }
          }
          
          /*if ((cter==d-1) && isnew){
              isexhaustive=false;
	      }*/
          
          if ((iv.el(cter)<n) && isnew){
              cter++; 
              iv.put(cter,0);
              q.mutate(0,1);
          }
          else if ((cter>0) && (iv.el(cter)<n)){
              q.mutate(iv.el(cter),1);
              iv.increase(cter);
              if (iv.el(cter)<n){q.mutate(iv.el(cter),1);}
          }
          else {
               cter=cter-1;
               if (cter>0){
		   q.mutate(iv.el(cter),1);
                   iv.increase(cter);
                   if (iv.el(cter)<n){q.mutate(iv.el(cter),1);}
                  }
              }
          }
      cter=0;
      QuiverSetSorter.sort(this);
      done=true;
      return new Boolean(isexhaustive);
    }

    public Boolean traverseHoriz(QuiverDrawing qd){
	
       done=false;
       canceled=false;
       boolean isexhaustive=true;
       AbstractQuiver aq=new AbstractQuiver(q,qd);
       aq.setcategory(category);
       boolean isnew=true;
      foundsofar=0;
      int n=aq.Vertices.length;
      //int[] i=new int[d];
      IntVector iv=new IntVector(depth);
      iv.addint(-1);
      //q.mutate(0);
      
      //System.out.println("qd is null :"+ (qd==null));
      
      AbstractQuiver memAq=null;
      if (memQuiver!=null){
          memAq=new AbstractQuiver(memQuiver,qd);
      }
      
      aq=new AbstractQuiver(q,qd);
      add(aq);
      int layerfrom=0;
      int layerto=0;
      
      
      
      cter=1;
      boolean stop=false;
      while(!canceled & !stop){
          
          int newlayerfrom=layerto+1;
          int newlayerto=newlayerfrom;
          int j=layerfrom;
          //System.out.println("Layer from:" + layerfrom+ " Layer to: " + layerto+
          //        " New layer from: "+newlayerfrom + " New layer to: " +newlayerto);
          while (j<=layerto){
              q=new Quiver(elementAt(j),qd);
              iv.clear();
              iv.addint(-1);
              if (elementAt(j).hist!=null){
                  for (int k=0;k<elementAt(j).hist.length;k++){
                      iv.addint(elementAt(j).hist[k]);
                  }
              }
              iv.addint(0);
              while (!canceled & (iv.el(cter)<n)){
                  isnew=true;
                  if (cter>0){
                      if (iv.el(cter)==iv.el(cter-1)){
                          isnew=false;
                      }
                  }
                  if (q.P[iv.el(cter)].frozen){
                      isnew=false;
                      //System.out.println("Vertex "+iv.el(cter)+" is frozen.");
                  }
                  if (onlygentle){
                      if (!q.isGentleVertex(iv.el(cter))){
                          isnew=false;
                      }
                  }
                  if (isnew){
                      if (iv.el(cter)>=0){
                          if (myTracker!=null){myTracker.mutate(q, iv.el(cter));}
                          q.mutate(iv.el(cter),1);
                          //System.out.println("Mutating at "+iv.el(cter));
                      }
                      if (MaxMult!=null){
                          if (q.M.maxMultExceeds(MaxMult).booleanValue()){
                              isnew=false;
                          }
                      }
                      if (isnew){
                          aq=new AbstractQuiver(q,qd);
                          isnew=add(aq);
                          if (isnew){
                              newlayerto++;
                              aq.sethistory(cter+1, iv);
                              if (memAq!=null){
                                  if (memAq.equals(aq)){
                                      stop=true;
                                  }
                              }
                              if (searchsinksources){
                                  if (q.sinksSources().length>0){
                                      absquiverWithSinkSource=aq;
                                      System.out.println("History: "+Utils.toString(aq.hist));
                                      stop=true;
                                  }
                              }
                          }
                      }
                      if (iv.el(cter)>=0){
                      if (myTracker!=null){myTracker.mutate(q, iv.el(cter));}
                      q.mutate(iv.el(cter),1);
                      //System.out.println("Mutating back at "+iv.el(cter));
                      }
                  }
                  iv.increase(cter);
              }
              j++;
          }
          layerfrom=layerto+1;
          layerto=newlayerto-1;
          cter++;
          if (layerto<layerfrom){
              stop=true;
          }
          if (MaxDepth!=null){
                      if (cter>MaxDepth.intValue()){
                          stop=true;
                      }
                  }
      }
      //QuiverSetSorter.sort(this);
      done=true;
      return new Boolean(isexhaustive);
    }
    
}    

class SkewReflection {
    int k;
    BigInteger[] v;
    
    public SkewReflection(int argk, BigInteger[] argv){
        k=argk;
        v=argv;
    }
    
    public String toString(){
        String s="Sign dependent on "+(k+1)+"\n";
        s=s+"Reflection vector:"+v[0].toString();
        for (int i=1; i<v.length;i++){
            s=s+","+v[i].toString();
        }
        return s;
    }
    
    public BigInteger[] apply(BigInteger[] w){
        BigInteger[] res=null;
        if (v.length!=w.length){
            System.out.println("Cannot apply reflection to vector:"+v.length+"<>"+w.length);
        }
        else {
        res=new BigInteger[w.length];
        for (int i=0; i<v.length;i++){
            if (i==k){
                res[i]=w[k].negate();
            }
            else {
            BigInteger m=new BigInteger(v[i].toString());
            if (w[k].compareTo(BigInteger.ZERO)>=0){
                m=m.negate();
            }
            m=Utils.max(m,BigInteger.ZERO);
            m=m.multiply(w[k]);
            res[i]=w[i].add(m);
            }
        }
        }
        return res;
    }
}

class CMatrix {
    int m;
    BMatrix[] A;
    
    public CMatrix(CMatrix C){
        m=C.m;
        A=new BMatrix[m+1];
        copyfrom(C);
    }
    
    public CMatrix(BufferedReader in){
        try{
            String str=in.readLine();
	    System.out.println("CMatrix 1: " + str);
	    m=Integer.parseInt(str);
            A=new BMatrix[m+1];
            for (int i=0; i<m+1; i++){
                str=in.readLine();
	        System.out.println("CMatrix "+ i+" :" + str);
                A[i]=new BMatrix(in);
            }
        }
        catch (IOException e){
	System.out.println(e.getMessage());
        }
    }
    
    public void write(BufferedWriter out){
	try{
            out.write("//Colored Matrix"); out.newLine();
            out.write(""+m); out.newLine();
            }
            catch (IOException e){
	    System.out.println(e.getMessage());
	}
        for (int i=0; i<m+1; i++){
            A[i].write(out);
        }
    }
    
    public CMatrix(int cd, BMatrix B){
        m=cd-1;
        A=new BMatrix[m+1];
        A[0]=new BMatrix(B.nbrows, B.nbcols);
        A[0].copyfrom(B);
        A[0].positivize();
        A[m]=new BMatrix(B.nbrows, B.nbcols);
        A[m].copyfrom(B);
        A[m].positivize();
        A[m].transpose();
        for (int c=1; c<m; c++){
            A[c]=new BMatrix(B.nbrows, B.nbcols);
            A[c].makeZero();
        }
        System.out.println(this);
    }
    
    public void copyfrom(CMatrix C){
        if (m!=C.m){
            System.out.println("Attempting to copy from different CY-dimension: "+m+"<>"+C.m);
            return;
        }
        for (int i=0; i<=m+1; i++){
            A[i].copyfrom(C.A[i]);
        }
    }
    
    public boolean equals(CMatrix C){
        if (m!=C.m){
            return false;
        }
        for (int i=0; i<=m+1; i++){
            if (!A[i].equals(C.A[i])){
                return false;
            }
        }
        return true;
    }
    
    public void toadjoint(){
        BMatrix[] B=new BMatrix[m+1];
        for (int i=0; i<m+1; i++){
            B[i]=new BMatrix(A[m-i]);
            //B[i].transpose();
        }
        A=B;   
        //System.out.println("Adjoint:\n"+this);
    }
    
    
    public void mutate(int k, int dir){
        if (dir==-1){toadjoint();}
        int nbrows=A[0].nbrows; 
        int nbcols=A[0].nbcols;
        BMatrix[] B=new BMatrix[m+1];
        for (int c=0; c<m+1; c++){
            B[c]=new BMatrix(nbrows, nbcols);
            for (int i=0; i<nbrows; i++){
                for (int j=0; j<nbcols; j++){
                    if (i==j){
                        B[c].A[i][j]=BigInteger.ZERO;
                    }
                    else {
                        if (k==j){
                            if (c>0){
                                B[c].A[i][j]=A[c-1].A[i][j];
                            }
                            else {
                                B[c].A[i][j]=A[m].A[i][j];  
                            }
                        }
                        if (k==i){
                            if (c<m){
                                B[c].A[i][j]=A[c+1].A[i][j];
                            }
                            else {
                                B[c].A[i][j]=A[0].A[i][j];
                            }
                        }
                        if ((k!=i)&(k!=j)){
                            BigInteger s=BigInteger.ZERO;
                            for (int c1=0; c1<m+1; c1++){
                                if (c1!=c){
                                    s=s.add(A[c1].A[i][j]);
                                }
                            }
                            s=A[c].A[i][j].subtract(s);
                            BigInteger d=BigInteger.ZERO;
                            if (c>0){
                                d=A[c-1].A[i][k].negate();
                            }
                            else {
                                d=A[m].A[i][k].negate();
                            }
                            d=d.add(A[c].A[i][k]);
                            d=d.multiply(A[0].A[k][j]);
                            s=s.add(d);
                            if (c<m){
                                d=A[c+1].A[k][j];
                            }
                            else {
                                d=A[0].A[k][j];
                            }
                            d=d.subtract(A[c].A[k][j]);
                            d=d.negate();
                            d=d.multiply(A[m].A[i][k]);
                            s=s.add(d);
                            if (s.compareTo(BigInteger.ZERO)>=0){
                                B[c].A[i][j]=s;
                            }
                            else {
                                B[c].A[i][j]=BigInteger.ZERO;
                            }
                        }
                    }
                }
            }
        }
        A=B;
        if (dir==-1){toadjoint();}
        System.out.println(this);
    }
    
    int getClusterdim(){
        return m+1;
    }
    
    boolean existArrows(int i, int j){
        boolean exist=false;
        for (int c=0; 2*c<m+1; c++){
            if (A[c].A[i][j].compareTo(BigInteger.ZERO)>0){exist=true;}
        }
        return exist;
    }
    
    boolean existMultArrows(int i, int j){
        boolean multexist=false;
        for (int c=0; 2*c<m+1; c++){
            if (A[c].A[i][j].compareTo(BigInteger.ONE)>0){multexist=true;}
        }
        return multexist;
    }
    
    String getArrows(int i, int j){
        String s=A[0].A[i][j].toString();
        for (int c=1; c<m+1; c++){
            //System.out.println("c="+c+":"+s);
            s=s+","+A[c].A[i][j].toString();
        }
        return s;
    }
    
    String getMultArrows(int i, int j){
        String s="";
        for (int c=0; c<m+1; c++){
            if (A[c].A[i][j].compareTo(BigInteger.ONE)>0){
            s=s+A[c].A[i][j].toString();}
        }
        return s;
    }
    
    Color getArrowColor(int i, int j){
        if (!existArrows(i,j)){
            return Color.BLACK;
            }
        else
            {
                int c=0;
                while (A[c].A[i][j].compareTo(BigInteger.ZERO)==0){c++;}
                if (c==0){return Color.black;}
                else {
                return new Color(Color.HSBtoRGB((2*(float) (c-1))/(m+1),1,1));
                }
             }
     }
        

    
    public String toString(){
        String s="";
        for (int i=0; i<A[0].nbrows; i++){
            for (int j=0; j<A[0].nbcols; j++){
                s=s+(i+1)+","+(j+1)+":"+ getArrows(i,j)+"\n";
            }
        }
        return s;
    }
}

class SMatrix {
    int [] [] M;
    
    public SMatrix(int[][] argM){
        M=new int[argM.length][argM[0].length];
        for (int i=0; i<argM.length;i++){
            for (int j=0; j<argM[i].length; j++){
                M[i][j]=argM[i][j];
            }
        }
    }
    
    public SMatrix(int n, int m){
        M=new int[n][m];
        int p,q;
            for (p=0; p<n; p++){
                for (q=0; q<m; q++){
                    M[p][q]=0;
                }
            }
    }
    
  public int getentry(int i, int j){
      return M[i][j];
    }
  
  public void setentry(int i, int j, int st){
      M[i][j]=st;
  }
  
  public String toString(){
      return Utils.toString(M);
  }
  
  public SMatrix(BufferedReader in){
     String str, patternstr;
     String[] fields;
     int i,j;
     
    try {
       str=in.readLine();
        patternstr=" ";
        fields = str.split(patternstr);
        int nbrows= Integer.parseInt(fields[0]);
        int nbcols= Integer.parseInt(fields[1]);
        M=new int[nbrows][nbcols];
        i=0;
        while (i<nbrows) {
            str=in.readLine();
            fields = str.split(patternstr);
            for(j=0; j<nbcols; j++){
            M[i][j]=Integer.parseInt(fields[j]);
        }
       i++;
    }
    }
    catch (IOException e) {
    System.err.println("File input error");
    }
 }

  
    public void read(BufferedReader in){
     String str, patternstr;
     String[] fields;
     int i,j;
     int[][] Mnew;
     
     Mnew=null;
     try {
	str=in.readLine();
	//System.out.println(str);
        str=in.readLine();
	//System.out.println(str);
        patternstr=" ";
        fields = str.split(patternstr);
        
        int nbrows= Integer.parseInt(fields[0]);
        int nbcols= Integer.parseInt(fields[1]);
	//System.out.println("nbrows:"+nbrows+" nbcols:"+nbcols);
        Mnew=new int[nbrows][nbcols];
        i=0;
        while (i<nbrows) {
            str=in.readLine();
            fields = str.split(patternstr);
            for(j=0; j<nbcols; j++){
            Mnew[i][j]=Integer.parseInt(fields[j]);
        }
        i++;
    }
    }
    catch (IOException e) {
    System.err.println("File input error");
    }
    if (Mnew!=null){
	M=null;
	M=Mnew;
    }
    }

public void write(BufferedWriter out){
	try{
	out.write("//Style Matrix"); out.newLine();
        int nbrows=M.length;
        int nbcols=M[0].length;
	out.write(nbrows + " " + nbcols); out.newLine();
	int i,j;
	for (i=0;i<nbrows;i++){
	    for (j=0;j<nbcols; j++){
		out.write(M[i][j]+" ");
	    }
	    out.newLine();
	}
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }

public void copyfrom(SMatrix B, int omit){
    int i,j, inew, jnew;

    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
    inew=0;
    int nbrows=B.M.length;
    int nbcols=B.M[0].length;
    for (i=0; i<nbrows;i++){
        if (i!=omit){
          jnew=0;
          for(j=0;j<nbcols;j++){
                if (j!=omit){
                    //System.out.println("inew, jnew" + inew + " " + jnew);
                            //System.out.println("i   , j   " + i + " " + j);
                    M[inew][jnew]=B.M[i][j];
                jnew++;
                }
              }
              inew++;
         }
    }
    //System.ou
    }
}

class BMatrix  { 
    int nbrows, nbcols;
    BigInteger[][] A; 
    
    public BMatrix subtract(BMatrix M){
        BMatrix S=new BMatrix(nbrows,nbcols);
        for (int i=0;i<nbrows;i++){
           for (int j=0;j<nbcols;j++){
               S.A[i][j]=A[i][j].subtract(M.A[i][j]);
           }
        }
        return S;
    }
    
    public boolean isconsistentat(int i0, int j0){
        System.out.println(this.toString());
        if (A[i0][j0].equals(BigInteger.ZERO)){
            return true;
        }
        String s; 
        boolean repeats=false; 
        int i=0;
        //System.out.println("repeats="+repeats + " i0="+i0 + " nbrows="+nbrows);
        while ((repeats!=true)&(i<nbrows)){
            if (i==i0){i++;}
            if (i<nbrows) 
                {repeats=(A[i0][j0].equals(A[i][j0]));}
            s="i0=" + i0+ " i=" + i + " A[i0,j0]" +  A[i][j0] +  " A[i][j0]"+  A[i][j0]+ " repeats="+repeats;
            //System.out.println(s);
            i++;}
        int j=0;
        while (repeats!=true & (j<nbcols)){
            if (j==j0){j++;}
            if (j<nbcols){
                repeats=(A[i0][j0].equals(A[i0][j]));
            }
            j++;
        }
        
        int i1=Math.round(i0/3)*3;
        int j1=Math.round(j0/3)*3;
        int k=0;
        
        
        while (repeats!=true & (k<9)){
           i=i1+Math.round(k/3);
           j=j1+ (k-Math.round(k/3)*3);
           System.out.println(""+i+" "+ j);
           if ((i!=i0)||(j!=j0))
               repeats=(A[i0][j0].equals(A[i][j]));
           k++;}
        
        
        return !repeats; 
    }
    
    public BMatrix makeconsistentat(int i0, int j0){
          int k=1;
          if ((!A[i0][j0].equals(BigInteger.ZERO))&isconsistentat(i0,j0)){
              return this;
          }
          A[i0][j0]=new BigInteger(""+k);
          while ((k<10) & !(isconsistentat(i0,j0))){
              k++; 
              A[i0][j0]=new BigInteger(""+k);
          }
          if (isconsistentat(i0,j0)) {
              return this ;}
          else return null;
}
    
    public BigInteger[] row(int k){
        BigInteger[] v = new BigInteger[nbcols];
        for (int i=0; i<nbcols; i++){
            v[i]=new BigInteger(A[k][i].toString());
        }
        return v;
    }
    
    public BigInteger[] column(int k){
        BigInteger[] v = new BigInteger[nbrows];
        for (int i=0; i<nbrows; i++){
            v[i]=new BigInteger(A[i][k].toString());
        }
        return v;
    }
    
    public boolean rowIsSimple(int i){
        BigInteger sumabs=BigInteger.ZERO;
        for (int j=0; j<nbcols; j++){
            if (A[i][j].compareTo(BigInteger.ZERO)<0){
                return false;
            }
            sumabs=sumabs.add(A[i][j].abs());
        }
        if (sumabs.compareTo(BigInteger.ONE)==0){
            return true;}
        else {
            return false;
        }
    }
    
    public boolean isSource(int i){
        boolean issource=true;
        for (int j=0; j<nbrows; j++){
            if (A[i][j].compareTo(BigInteger.ZERO)<0){
                issource=false;
            }
        }
        return issource;
    }
    
    public boolean isSourceInComplement(int i, Vector v){
        boolean issource=true;
        boolean isInV;
        for (int j=0; j<nbrows; j++){
            if ((!v.contains(new Integer(j)))&&(A[i][j].compareTo(BigInteger.ZERO)<0)){
                issource=false;
            }
        }
        return issource;
    }
    
    int[] sourcesInComplement(Vector v){
        int[] sc=new int[nbrows];
        int cter=0;
        for (int i=0; i<nbrows; i++){
            if (isSourceInComplement(i,v)){
                sc[cter]=i;
                cter++;
            }
        }
        int[] src=new int[cter];
        for (int i=0; i<cter; i++){
            src[i]=sc[i];
        }
        return src;
    }
    
    
    public boolean isSink(int i){
        boolean issource=true;
        for (int j=0; j<nbrows; j++){
            if (A[i][j].compareTo(BigInteger.ZERO)>0){
                issource=false;
            }
        }
        return issource;
    }
    
    public int[] sources(){
        int[] sc=new int[nbrows];
        int cter=0;
        for (int i=0; i<nbrows; i++){
            if (isSource(i)){
               sc[cter]=i;
               cter++;
            }
        }
        if (cter==0){return null;}
        int[] src=new int[cter];
        for (int i=0; i<cter; i++){
            src[i]=sc[i];
        }
        return src;
    }
    
    public int[] sinks(){
        int[] sn=new int[nbrows];
        int cter=0;
        for (int i=0; i<nbrows; i++){
            if (isSink(i)){
               sn[cter]=i;
               cter++;
            }
        }
        if (cter==0){return null;}
        int[] snk=new int[cter];
        for (int i=0; i<cter; i++){
            snk[i]=sn[i];
        }
        return snk;
    }
    
    public int[] immediatePredec(int i){
        Vector v=new Vector(nbrows);
        for (int j=0;j<nbrows; j++){
            if (A[j][i].compareTo(BigInteger.ZERO)>0) {
                 v.add(new Integer(j));
            }
        }
        int[] pred=new int[v.size()];
        for (int j=0; j<v.size();j++){
            pred[j]=((Integer) v.elementAt(j)).intValue();
        }
        return pred;
    }
    
    public int[] immediateSucc(int i){
        Vector v=new Vector(nbrows);
        for (int j=0;j<nbrows; j++){
            if (A[j][i].compareTo(BigInteger.ZERO)<0) {
                 v.add(new Integer(j));
            }
        }
        if (v.size()==0){ 
            return null;
        }
        int[] succ=new int[v.size()];
        for (int j=0; j<v.size();j++){
            succ[j]=((Integer) v.elementAt(j)).intValue();
        }
        return succ;
    }
    
    public void heightOfBranch(int previousvertex, int vertex, int[] height){
        String s="";
        for (int i=0;i<nbrows;i++){
            s=s+" "+height[i];
        }
        System.out.println(s);
        System.out.println(previousvertex+" "+vertex);
        
        int[] succ=immediateSucc(vertex);
        if (succ!=null){
            for (int i=0;i<succ.length;i++){
                if (succ[i]!=previousvertex){
                    height[succ[i]]=height[vertex]+1;
                    heightOfBranch(vertex, succ[i],height);
                }
            }
        }
        int[] predec=immediatePredec(vertex);
        if (predec!=null){
            for (int i=0;i<predec.length;i++){
                if (predec[i]!=previousvertex){
                    height[predec[i]]=height[vertex]-1;
                    heightOfBranch(vertex, predec[i], height);
                }
            }
        }
    }
    
    public int[] heightfunction(){
        int[] height=new int[nbrows];
        height[0]=0;
        heightOfBranch(0,0,height);
        
        int min=nbrows;
        for (int i=0;i<nbrows;i++){
            if (height[i]<min){
                min=height[i];
            }
        }
        for (int i=0;i<nbrows;i++){
            height[i]=height[i]-min;
        }
        return height;
    }
    
    
    
    public int[] successorChain(int i){
        Vector v=new Vector(nbrows);
        int[] succ=immediateSucc(i);
        //System.out.println("Immediate successors of "+i+": "+Utils.toString(succ));
        while (succ!=null){
           int nextsucc=succ[0];
           v.add(new Integer(nextsucc));
           succ=immediateSucc(nextsucc);
           //System.out.println("Immediate successors of "+nextsucc+": "+Utils.toString(succ));
        }
        if (v.size()==0){ 
            return null;
        }
        int[] succchain=new int[v.size()];
        for (int j=0; j<v.size();j++){
            succchain[j]=((Integer) v.elementAt(j)).intValue();
        }
        return succchain;
    }
    
    public void enterMatrix(Frame f, String msg){
        String str=JOptionPane.showInputDialog(f,msg+"\n"+nbrows+" x "+nbcols+"-matrix:\nType the matrix entries\n"   
     + "row by row separated by spaces");
        if (str!=null){
            String[] fields=str.split(" ");
            if (fields.length!=nbcols*nbrows){
                JOptionPane.showMessageDialog(f, "You typed "+ fields.length+ " entries instead of "+ (nbrows*nbcols)+".");
            }
            else {
                int cter=0;
                for (int i=0; i<nbrows; i++){
                    for (int j=0; j<nbcols; j++){
                        A[i][j]=new BigInteger(fields[cter]);
                        cter++;
                    }
                }
            }
        }
    }
    
    public void symmetrize(){
        int n=nbrows;
        BigInteger[][] B=new BigInteger[n][n];
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                    B[i][j]=A[i][j].add(A[j][i]);
            }
        }
        A=null;
        A=B;
    }

    public void read(BufferedReader in){
     String str, patternstr;
     String[] fields;
     int i,j;
     BigInteger[][] Anew;
     
     Anew=null;
     try {
	str=in.readLine();
	//System.out.println(str);
        str=in.readLine();
	//System.out.println(str);
        patternstr=" ";
        fields = str.split(patternstr);
        nbrows= Integer.parseInt(fields[0]);
        nbcols= Integer.parseInt(fields[1]);
	//System.out.println("nbrows:"+nbrows+" nbcols:"+nbcols);
        Anew=new BigInteger[nbrows][nbcols];
        i=0;
        while (i<nbrows) {
            str=in.readLine();
            fields = str.split(patternstr);
            for(j=0; j<nbcols; j++){
        Anew[i][j]=new BigInteger(fields[j]);
        }
            i++;
    }
    }
    catch (IOException e) {
    System.err.println("File input error");
    }
    if (Anew!=null){
	A=null;
	A=Anew;
    }
    }

public void write(BufferedWriter out){
	try{
	out.write("//Matrix"); out.newLine();
	out.write(nbrows + " " + nbcols); out.newLine();
	int i,j;
	for (i=0;i<nbrows;i++){
	    for (j=0;j<nbcols; j++){
		out.write(A[i][j]+" ");
	    }
	    out.newLine();
	}
	}
	catch (IOException e){
	    System.out.println(e.getMessage());
	}
    }
    public String qysystem(){
        String s="sols:=solve({";
        String t="subs(sols,{";
        int i,j;
        BigInteger m=null;
        for (i=0; i<nbrows; i++){
            s=s+"y"+(i+1)+"=(q"+(i+1)+"-1)";
            for (j=0; j<nbcols;j++){
                m=A[i][j];
                if (m.compareTo(BigInteger.ZERO)>0){
                   s=s+"/q"+(j+1);
                }
                if (m.compareTo(BigInteger.ONE)>0){
                   s=s+m;
                }
            }
            if (i<nbrows-1){
                s=s+",";
            }
            else {
                s=s+"},{";
            }
            t=t+"z"+(i+1)+"=1/(q"+(i+1)+"-1)";
            for (j=0; j<nbcols;j++){
                m=A[j][i];
                if (m.compareTo(BigInteger.ZERO)>0){
                   t=t+"*q"+(j+1);
                }
                if (m.compareTo(BigInteger.ONE)>0){
                   t=t+"^"+m;
                }
            }
            if (i<nbrows-1){
                t=t+",";
            }
        }
        for (i=0; i<nbrows; i++){
            s=s+"q"+(i+1);
            if (i<nbrows-1){
                s=s+",";
            }
        }
        t=t+"})";
        t="simplify("+t+");";
        s=s+"}); \n"+t;
        return s;
    }
    
    

    public void copyfrom(BMatrix B){
    int i,j;

    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
    for (i=0; i<B.nbrows;i++){
        for(j=0;j<B.nbcols;j++){
            A[i][j]=null;
	    A[i][j]=new BigInteger(B.A[i][j].toString());
            }
    }
    }
    
    public void transpose(){
    int i,j;
        //System.out.println("To transpose:\n"+this);
        BMatrix M=new BMatrix(nbcols, nbrows);
        for (i=0; i<nbrows; i++){
            for (j=0; j<nbcols; j++){
                M.A[j][i]=A[i][j];
            }
        }
        nbrows=M.nbrows;
        nbcols=M.nbcols;
        A=M.A;
        //System.out.println("Transposed:\n"+this);
    }
    
    public void copyPosPartfrom(BMatrix B){
    int i,j;

    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
    for (i=0; i<B.nbrows;i++){
        for(j=0;j<B.nbcols;j++){
            A[i][j]=null;
	    A[i][j]=new BigInteger(B.A[i][j].toString());
            if (A[i][j].signum()<0){
                A[i][j]=BigInteger.ZERO;
            }
            }
    }
    }
    
    public void positivize(){
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                //System.out.println("Positivizing: "+i+","+j);
                if (A[i][j].compareTo(BigInteger.ZERO)<0){A[i][j]=BigInteger.ZERO;}
            }
        }
    }
    
    public BigInteger gcd(){
        BigInteger gcd=BigInteger.ZERO;
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                gcd=A[i][j].gcd(gcd);
            }
        }
        return gcd;
    }
    
    public void antisymmetrize(){
        BigInteger[][] B=new BigInteger[nbrows][nbcols];
        for (int i=0; i<nbrows; i++){
            for (int j=i; j<nbcols; j++){
                B[i][j]=A[i][j].subtract(A[j][i]);
                B[j][i]=B[i][j].negate();
            }
        }
        A=B;
    }
    
    public void copyCartanCompanionFrom(BMatrix B){
    int i,j;

    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
    for (i=0; i<B.nbrows;i++){
        for(j=0;j<B.nbcols;j++){
            A[i][j]=null;
	    A[i][j]=new BigInteger(B.A[i][j].toString());
            if (A[i][j].signum()>0){
                A[i][j]=A[i][j].negate();
            }
            if (i==j){A[i][j]=new BigInteger("2");}
            }
    }
    }
    
    public void makeZero(){
        int i,j;
        for (i=0; i<nbrows; i++){
            for (j=0; j<nbcols; j++){
                A[i][j]=BigInteger.ZERO;
            }
        }
    }
    
    public void addmulrow(BigInteger c, int i1, int i2){
    // add c times row i1 to row i2
        int j;
        for (j=0; j<nbcols; j++){
            A[i2][j]=A[i1][j].multiply(c).add(A[i2][j]);
        }
    }
    
    public void negate(){
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                A[i][j]=A[i][j].negate();
            }
        }
    }
    
    public BMatrix extract(int firstrow, int lastrow, int firstcolumn, int lastcolumn){
        BMatrix R=new BMatrix(lastrow-firstrow, lastcolumn-firstcolumn);
        for (int i=0; i<lastrow-firstrow; i++){
            for (int j=0; j<lastcolumn-firstcolumn; j++){
                R.A[i][j]=new BigInteger(A[firstrow+i][firstcolumn+j].toString());
            }
        }
        return R;
    }
    
    public BigInteger lcmEntries(){
        BigInteger lcm=BigInteger.ONE;
        BigInteger gcd=null;
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                if (A[i][j].compareTo(BigInteger.ZERO)!=0){
                    gcd=A[i][j].gcd(lcm);
                    lcm=A[i][j].multiply(lcm).divide(gcd);
                }
            }
        }
        return lcm;
    }
    
    public boolean equals(BMatrix M){
        if (nbcols!=M.nbcols){
            return false;
        }
        if (nbrows!=M.nbrows){
            return false;
        }
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                if (!A[i][j].equals(M.A[i][j])){
                    return false;
                }
            }
        }
        return true;
    }
    
    public static BMatrix IdentityMatrix(int n){
        BMatrix M=new BMatrix(n,n);
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (i==j){
                    M.A[i][j]=BigInteger.ONE;
                }
                else {
                    M.A[i][j]=BigInteger.ZERO;
                }
            }
        }
        return M;
    }
    
    public double[][] toDouble(){
        double[][] D=new double[nbrows][nbcols];
        for (int i=0; i<nbrows;i++){
            for (int j=0; j<nbcols;j++){
                D[i][j]=A[i][j].doubleValue();
            }
        }
        return D;
    }
    
    public Jama.Matrix toJama(){
        return new Jama.Matrix(toDouble());
    }
    
    public BMatrix adjoint(){
        Jama.Matrix J=toJama();
        BMatrix L=null;
        double d=J.det();
        System.out.println("Matrix:");
        J.print(7,3);
        System.out.println("Determinant = "+d);
        if (Math.abs(d)>0.001){
            Jama.Matrix K=J.inverse();
            System.out.println("Inverse:");
            K.print(7,3);
            K=K.times(d);
            K.print(7,3);
            L=new BMatrix(nbrows,nbcols);
            for (int i=0; i<nbrows; i++){
                for (int j=0; j<nbcols; j++){
                    L.A[i][j]=new BigInteger(""+Math.round(K.get(i,j)));
                }
            }
            System.out.println("Matrix L\n"+L);
            BMatrix U=new BMatrix(this);
            U.multiplyby(L);
            System.out.println("L times M\n"+U);
        }
        return L;
    }
    
    public static BigInteger[] FindRecurrence(String s){
        String fields[];
        String patternstr=",";
        
        fields=s.split(patternstr);
        int n=fields.length;
        RingElt[] a=new RingElt[n];
        
        for (int i=0; i<fields.length; i++){
            a[i]=Ring.Q.map(fields[i]);
        }
        
        RingElt den=Ring.Q.one();
        RingElt gcd=Ring.Z.one();
        RingElt lcm=Ring.Z.one();
        for (int i=0; i<n; i++){
            gcd=Ring.Z.gcd(lcm, Ring.Z.map(Ring.Q.denominatorToBigInteger(a[i])));
            lcm=Ring.Z.mult(den,lcm);
            lcm=Ring.Z.div(lcm,gcd);
        }
        
        BigInteger[] b=new BigInteger[n];
        for (int i=0; i<n; i++){
            a[i]=Ring.Q.mult(a[i],Ring.Q.map(lcm));
            b[i]=Ring.Q.numeratorToBigInteger(a[i]);
        }
        
        return FindRecurrence(b);
    }
    
    
    
    public static BigInteger[] FindRecurrence(BigInteger[] a){
      int n=(1+a.length)/2;
      
      if (n<=1){return null;}
      
      BigInteger[][] B=new BigInteger[n][n];
      for (int i=0; i<n; i++){
          for (int j=0; j<n; j++){
              B[i][j]=new BigInteger(a[i+j].toString());
          }
      }
      BMatrix A=new BMatrix(B);
      System.out.println(A.toString());
      BMatrix K=null;
      BMatrix E=null;
      boolean foundrelation=false;
      for (int m=1; m<=A.nbcols; m++){
          System.out.println("m="+m);
          E=A.extract(0,m,0,m);
          System.out.println(E);
          K=E.Kernel();
          if (K==null){
              System.out.println("Kernel is null.");
          }
          
          if (K!=null){
              E=A.extract(0,A.nbrows, 0,m);
              System.out.println(E);
              System.out.println("E:"+E.nbrows+"," +E.nbcols);
              System.out.println("K:"+K.nbrows+","+K.nbcols);
              E.multiplyby(K);
              System.out.println("E:"+E.nbrows+"," +E.nbcols);
              System.out.println(E);
              foundrelation=E.isZero();
          }
          if (foundrelation){break;}
      }
      
      if (K!=null){
        System.out.println(K.toString());
        return K.column(0);
      }
      else {
          System.out.println("Kernel is null.");
          return null;
      }
    }
    
    public void addIdentity(){
    int i;
    for (i=0; i<nbrows; i++){
        A[i][i]=A[i][i].add(BigInteger.ONE);
    }
    }
    
    public void appendIdentityBelow(){
        transpose();
        appendIdentity();
        transpose();
    }

    public void insertRow(BigInteger[] newrow, int insati ){
        BMatrix B=new BMatrix(nbrows+1,nbcols);
        B.makeZero();
        //System.out.println("New matrix 0: "+B);
        int i,j;
        for (i=0; i<nbrows+1; i++){
            if (i<insati){
            for (j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(A[i][j].toString());
            }}
            if (i==insati){
            for (j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(newrow[j].toString());
            }}
            if (i>insati){
            for (j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(A[i-1][j].toString());
            }}
        }
        //System.out.println("New matrix first block: "+B);

        System.out.println("Insertrow: New matrix total: \n"+B);
        A=null;
        A=B.A;
        nbrows=nbrows+1;
    }

    public void appendRow(BigInteger[] newrow ){
        BMatrix B=new BMatrix(nbrows+1,nbcols);
        B.makeZero();
        //System.out.println("New matrix 0: "+B);
        int i,j;
        for (i=0; i<nbrows; i++){
            for (j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(A[i][j].toString());
            }
        }
        //System.out.println("New matrix first block: "+B);
         i=nbrows;
         for (j=0; j<nbcols; j++){
           B.A[i][j]=new BigInteger(newrow[j].toString());
         }

        System.out.println("New matrix total: \n"+B);
        A=null;
        A=B.A;
        nbrows=nbrows+1;
    }
    
    public void appendBlock(BMatrix C){
        BMatrix B=new BMatrix(nbrows+C.nbrows,nbcols+C.nbcols);
        B.makeZero();
        System.out.println("New matrix 0: "+B);
        int i,j;
        for (i=0; i<nbrows; i++){
            for (j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(A[i][j].toString());
            }
        }
        System.out.println("New matrix first block: "+B);
         for (i=0; i<C.nbrows;i++){
             for (j=0; j<C.nbcols; j++){
                 B.A[i+nbrows][j+nbcols]=new BigInteger(C.A[i][j].toString());
             }
           }
        System.out.println("New matrix total: "+B);
        A=null;
        A=B.A;
        nbrows=nbrows+C.nbrows;
        nbcols=nbcols+C.nbcols;
    }
    
    public void appendIdentity(){
        BMatrix B=new BMatrix(nbrows,nbcols+nbrows);
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols;j++){
                B.A[i][j]=new BigInteger(A[i][j].toString());
            }
            for (int j=nbcols; j<nbrows+nbcols; j++){
                if (j==i+nbcols){
                    B.A[i][j]=BigInteger.ONE;
                }
                else {
                    B.A[i][j]=BigInteger.ZERO;
                }
            }
        }
        A=null;
        A=B.A;
        nbcols=nbcols+nbrows;
    }
    
    void mulrow(BigInteger c, int i){
        for (int j=0; j<nbcols; j++){
            A[i][j]=A[i][j].multiply(c);
        }
    }
    
    boolean isZeroBelow(int i1, int j1){
        boolean z=true;
        for (int i=i1; i<nbrows; i++){
            if (A[i][j1].compareTo(BigInteger.ZERO)!=0){
                z=false;
            }
        }
        return z;
    }
    
    boolean isAntisymmetric(){
        if (nbrows!=nbcols){return false;}
        boolean z=true;
        for (int i=0;i<nbrows;i++){
            for (int j=i;j<nbcols; j++){
                if (A[i][j].negate().compareTo(A[j][i])!=0){
                    z=false;
                }
            }
        }
        return z;
    }
    
    boolean isZero(){
        boolean z=true;
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++)
                if (A[i][j].compareTo(BigInteger.ZERO)!=0){
                    z=false;
            }
        }
        return z;
    }
    
    boolean isPositive(){
        boolean p=true;
        for (int i=0; i<nbrows; i++){
            for (int j=0; j<nbcols; j++){
                if (A[i][j].signum()<0){
                    p=false;
                }
            }
        }
        return p;
    }
    
    BigInteger gcdCol(int i1, int j1){
        // returns the gcd of the a_{i,j1} where i>=i1
        BigInteger g=new BigInteger(A[i1][j1].toString());
        for (int i=i1; i<nbrows; i++){
            g=g.gcd(A[i][j1]);
        }
        return g;
    }
    
    int minRowIndex(int i1,int col){
        // returns the index i>=i1 such that a_{i,col} has minimal strictly positive absolute value
        // returns nbrows if all a_{i,col} vanish for i>=i1
        int minindex=i1;
        BigInteger minval= A[minindex][col].abs();
        while ((minval.compareTo(BigInteger.ZERO)==0) & (minindex<nbrows)){
            minindex=minindex+1;
            if (minindex<nbrows){
                minval=A[minindex][col].abs();
            }
        }
        if (minindex<nbrows){
        for (int i=minindex+1; i<nbrows; i++){
            BigInteger absval=A[i][col].abs();
            if ((BigInteger.ZERO.compareTo(absval)<0) && (absval.compareTo(minval)<0)){
                minindex=i;
                minval=absval;
            }
        }
        }
        if (minindex>=nbrows){
            System.out.println("No non zero entries below "+i1+" in column "+col);
        }
        return minindex;
    }
    
    void invert() throws Exception {
       if (nbrows!=nbcols){
           throw new Exception("Matrix not invertible since not square: "+nbrows+" x "+nbcols);
       }
       appendIdentity();
       rowEchelonForm();
       for (int i=0; i<nbrows;i++){
           if (A[i][i].compareTo(BigInteger.ONE)!=0){
               throw new Exception("Matrix not invertible: \n" +toString());
           }
       }
       BigInteger[][] B=new BigInteger[nbrows][nbrows];
       for (int i=0; i<nbrows;i++){
           for (int j=0; j<nbrows; j++){
               B[i][j]=new BigInteger(A[i][j+nbrows].toString());
           }
       }
       A=null;
       A=B;
       nbcols=nbcols-nbrows;
    }
    
    void columnEchelonForm(){
        transpose();
        rowEchelonForm();
        transpose();
    }
    
    BMatrix Kernel(){
        BMatrix BM=new BMatrix(this);
        BM.appendIdentityBelow();
        BM.columnEchelonForm();
        int j=-1;
        boolean topzero=false;
        while ((j<nbcols-1) && (!topzero)) {
            j=j+1;
            topzero=true;
            for (int i=0; i<nbrows; i++){
                if (BM.A[i][j].compareTo(BigInteger.ZERO)!=0){
                topzero=false;
                }
            }
        }
        if (!topzero){
            return null;
        }
        else {
            //System.out.println(this);
            //System.out.println("nbrows="+nbrows+" 2*nbrows="+2*nbrows+" j="+j+" nbcols="+nbcols);
            return BM.extract(nbrows, 2*nbrows, j, nbcols);
        }
    }
    
    public String decompVector(BigInteger[] v){
        String branch="";
        String[] result=new String[1];
        result[0]="";
        decompVector(v,branch,result);
        return result[0];
    }
    
    public void decompVector(BigInteger[] v, String branch, String[] result){
        int m=nbrows;
        int n=nbcols;
        if (v.length!=n){result=null; return;}
        boolean iszero=true;
        int j=0;
        while ((j<n)&&(iszero)){
            if (v[j].compareTo(BigInteger.ZERO)!=0){iszero=false;}
            j++;
        }
        if (iszero){
            result[0]=result[0]+branch+", ";
            System.out.println("New result: "+result[0]);
            return;
        }
        int start=0;
        if (branch.length()>0){
            String patternstr=" ";
            String[] fields=branch.split(patternstr);
            start=Integer.parseInt(fields[fields.length-1]);
        }
        for (int i=start; i<m; i++){
            BigInteger[] w=new BigInteger[n];
            boolean ispositive=true;
            for (j=0; j<n; j++){
                w[j]=v[j].subtract(A[i][j]);
                ispositive=ispositive && (w[j].compareTo(BigInteger.ZERO)>=0);
            }
            if (ispositive){
                //System.out.println("Subtracted:"+(i+1)+"\nTo decompose:"+Utils.toString(w));
                if (branch.length()>0){
                    branch=branch+" "+(i+1);
                }
                else {
                    branch=" "+(i+1);
                }
                System.out.println("New branch: "+branch);
                decompVector(w, branch, result);
                branch=branch.subSequence(0,branch.lastIndexOf(" ")).toString();
            }
        }
    }
    
    
    void rowEchelonForm(){
        int colcounter=0;
        int rowcounter=0;
        while ((colcounter<nbcols) && (rowcounter<nbrows)){
            int i=rowcounter;
            int j=colcounter;
            
            boolean z=isZeroBelow(i,j);
            while (z && (j<nbcols)){
                j=j+1;
                if (j<nbcols){
                    z=isZeroBelow(i,j);
                }
            }
            
             if (j<nbcols){
                BigInteger g=gcdCol(i,j);
                do {
                    int mri=minRowIndex(i,j);
                    swapRows(mri,i);
                    if (A[i][j].compareTo(BigInteger.ZERO)<0){
                        mulrow(BigInteger.ONE.negate(),i);
                    }
                    rowPivot(i,j);
                    //System.out.println("Echelon: i="+i+"  j="+j);
                    //System.out.println(toString());
                }
                while (g.compareTo(A[i][j])<0);
            }
            colcounter=j+1;
            rowcounter=rowcounter+1;
            }
    }
    
    
    void divideElementsBy(BigInteger d){
        for (int i=0; i<nbrows;i++){
            for (int j=0;j<nbcols;j++){
                A[i][j]=A[i][j].divide(d);
            }
        }
    }
    
    
    void multiplyby(BMatrix B){
    int i,j, k;
    
    BigInteger[][] C=new BigInteger[nbrows][B.nbcols];
    for (i=0; i<nbrows; i++){
        for (j=0; j<B.nbcols;j++){
            C[i][j]=new BigInteger("0");
            for (k=0;k<nbcols; k++){
                C[i][j]=C[i][j].add(A[i][k].multiply(B.A[k][j]));
                //System.out.println(""+i+" "+j+" " + C[i][j]);
            }
        }
    }
    A=null;
    A=C;
    nbcols=B.nbcols;
    }
    
    void leftmultiplyby(BMatrix B){
    int i,j, k;
    
    BigInteger[][] C=new BigInteger[B.nbrows][nbcols];
    for (i=0; i<B.nbrows; i++){
        for (j=0; j<nbcols;j++){
            C[i][j]=BigInteger.ZERO;
            for (k=0;k<B.nbcols; k++){
                C[i][j]=C[i][j].add(B.A[i][k].multiply(A[k][j]));
                //System.out.println(""+i+" "+j+" " + C[i][j]);
            }
        }
    }
    A=C;
    nbrows=B.nbrows;
    }
    
    
    public BMatrix valencies(){
    BMatrix V;

        BMatrix T=new BMatrix(nbrows,nbcols);
        T.copyfrom(this);
        
        /*for (int i=0; i<T.nbrows; i++){
            for (int j=0; j<T.nbcols; j++){
                if (T.A[i][j].compareTo(BigInteger.ZERO)<0){T.A[i][j]=null; T.A[i][j]=new BigInteger("0");}
            }
        }*/
        
        V=new BMatrix(nbrows,nbcols);
        for (int j=0;j<nbcols; j++){
            for (int i=0;i<nbrows; i++){
              V.A[i][j]=new BigInteger("0");
              for (int k=0; k<nbcols;k++){
                  V.A[i][j]=V.A[i][j].add(T.A[i][k]);
              }
            }
            T.multiplyby(this);
            // java.util.List L=Arrays.asList(T.A);
            //Collections.sort(L);
            //System.out.println(L);
        }
        return V;
    }
    
    public void copyfrom(BMatrix B, int omit){
    int i,j, inew, jnew;

    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
        inew=0;
    for (i=0; i<B.nbrows;i++){
        if (i!=omit){
          jnew=0;
          for(j=0;j<B.nbcols;j++){
        if (j!=omit){
            //System.out.println("inew, jnew" + inew + " " + jnew);
                    //System.out.println("i   , j   " + i + " " + j);
	    A[inew][jnew]=new BigInteger(B.A[i][j].toString());
        jnew++;
        }
          }
          inew++;
        }
    }
    //System.out.println(""+A);
    }

    public void swapRows(int i1, int i2){
        BigInteger temp;
        for (int j=0; j<nbcols; j++){
            temp=A[i1][j];
            A[i1][j]=A[i2][j];
            A[i2][j]=temp;
        }
    }
    
    public BMatrix removeRow(int row){
        int[] rows= new int[1];
        rows[0]=row;
        return removeRows(rows);
    }
    
    public BMatrix removeRows(int[] rows){
        if (rows.length==0){
            return new BMatrix(this);
        }
        BMatrix B=new BMatrix(nbrows-rows.length,nbcols);
        int cter=0;
        int i1=0;
        int i=0;
        while (i1<nbrows-rows.length){
            //System.out.println("i="+i+", i1="+i1+", cter="+cter+", rows[cter]="+rows[cter]);
            if (i==rows[cter]) {
                if (cter<rows.length-1) {cter++;}
            }
            else {
                for (int j=0; j<nbcols; j++){
                    B.A[i1][j]=new BigInteger(A[i][j].toString());
                }
                i1++;
            }
            i++;
        }
        return B;
    }
    
    public BMatrix removeColumns(int[] cols){
        transpose();
        BMatrix B=removeRows(cols);
        transpose();
        return B;
    }
    
    public BMatrix removeRowsCols(int[] indices){
        BMatrix B=removeRows(indices);
        //System.out.println(this.toString());
        B=B.removeColumns(indices);
        //System.out.println(this.toString());
        return B;
    }
    
    public boolean isAcyclic(){
        BMatrix T=new BMatrix(this);
        int[] sinks=T.sinks();
        //System.out.println("Sinks: "+Utils.toString(sinks));
        while (sinks!=null){
            T=T.removeRowsCols(sinks);
            sinks=T.sinks();
            //System.out.println("Sinks: "+Utils.toString(sinks));
        }
        return (T.nbrows==0);
    }
    
    public void rowPivot(int ip, int jp){
        for (int i=0; i<nbrows; i++){
            if (i==ip){
                continue;
            }
            BigInteger q=A[i][jp].divide(A[ip][jp]);
            if (q.compareTo(BigInteger.ZERO)!=0){
                addmulrow(q.negate(), ip, i);
            }
        }
    }

    public void permuteRows(int[] perm){
        BigInteger[][] B=new BigInteger[nbrows][nbcols];
        //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
        //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
        int i,j;
        for (i=0; i<nbrows;i++){
              for(j=0;j<nbcols;j++){
              B[i][j]=A[perm[i]][j];
              //System.out.println("i="+i+" j="+j+"p[i]="+rowperm[i]+" p[j]="+rowperm[j]+"A[i,j]=" +A[i][j]);
            }
        }
        A=null;
        A=B;
    }

    public void permuteCols(int[] perm){
        BigInteger[][] B=new BigInteger[nbrows][nbcols];
        //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
        //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
        int i,j;
        for (i=0; i<nbrows;i++){
              for(j=0;j<nbcols;j++){
              B[i][j]=A[i][perm[j]];
              //System.out.println("i="+i+" j="+j+"p[i]="+rowperm[i]+" p[j]="+rowperm[j]+"A[i,j]=" +A[i][j]);
            }
        }
        A=null;
        A=B;
    }
    
    public void permuteRowsCols(int[] rowperm, int[] colperm){
    int i,j;
    
     /*String s=""+rowperm[0];
     String t=""+colperm[0];
     for (i=1; i<nbrows;i++){
          s=s+","+rowperm[i];
          t=t+","+colperm[i];
     }
     //System.out.println("rowperm"+s+"\ncolperm"+t);
      */
    BigInteger[][] B=new BigInteger[nbrows][nbcols];
    //System.out.println("Columns: new"+nbrows+" old "+B.nbrows);
    //System.out.println("Rows   : new"+nbcols+" old "+B.nbcols);
    for (i=0; i<nbrows;i++){
          for(j=0;j<nbcols;j++){
	  B[i][j]=A[rowperm[i]][colperm[j]];
          //System.out.println("i="+i+" j="+j+"p[i]="+rowperm[i]+" p[j]="+rowperm[j]+"A[i,j]=" +A[i][j]);
        }
    }
    A=null;
    A=B;
    //System.out.println(""+A);
    }

 public BMatrix(int p, int q){ 
     nbrows=p; nbcols=q; 
     A=new BigInteger[nbrows][nbcols];
 }
 
 public BMatrix(BMatrix B){
     nbrows=B.nbrows;
     nbcols=B.nbcols;
     A=new BigInteger[nbrows][nbcols];
     for (int i=0; i<nbrows; i++){
     for (int j=0; j<nbcols; j++){
         A[i][j]=new BigInteger(B.A[i][j].toString());
     }
     }
 }
 
 public BMatrix(BigInteger[][] B){
     nbrows=B.length;
     nbcols=B[0].length;
     A=new BigInteger[nbrows][nbcols];
     for (int i=0; i<nbrows; i++){
     for (int j=0; j<nbcols; j++){
         A[i][j]=new BigInteger(B[i][j].toString());
     }
     }
 }
 
 public BMatrix(BigInteger[] B){
     nbrows=B.length;
     nbcols=1;
     A=new BigInteger[nbrows][nbcols];
     for (int i=0; i<nbrows; i++){
         A[i][0]=new BigInteger(B[i].toString());
     }
 }

 public BMatrix(BufferedReader in){
     String str, patternstr;
     String[] fields;
     int i,j;
     
    try {
       str=in.readLine();
        patternstr=" ";
        fields = str.split(patternstr);
        nbrows= Integer.parseInt(fields[0]);
        nbcols= Integer.parseInt(fields[1]);
        A=new BigInteger[nbrows][nbcols];
        i=0;
        while (i<nbrows) {
            str=in.readLine();
            fields = str.split(patternstr);
            for(j=0; j<nbcols; j++){
        A[i][j]=new BigInteger(fields[j]);
        }
            i++;
    }
    }
    catch (IOException e) {
    System.err.println("File input error");
    }
 }

 

 public BMatrix(String infilename){
     String str, patternstr;
     String[] fields;
     int i,j;

    try {
        BufferedReader in = new BufferedReader(new FileReader(infilename));
        str=in.readLine();
        patternstr=" ";
        fields = str.split(patternstr);
        nbrows= Integer.parseInt(fields[0]);
        nbcols= Integer.parseInt(fields[1]);
        A=new BigInteger[nbrows][nbcols];
        i=0;
        while ((str = in.readLine()) != null && i<nbrows) {
            fields = str.split(patternstr);
            for(j=0; j<nbcols; j++){
        A[i][j]=new BigInteger(fields[j]);
        }
            i++;
    }
    in.close();
    }
    catch (IOException e) {
    System.err.println("File input error");
    }
 }

 public void assign(int[] coeff){
    int i,j;
    for(i=0; i<nbrows; i++){
       for(j=0; j<nbcols; j++){
       A[i][j]=new BigInteger(""+coeff[i*nbcols+j]);}}
 }
 
 public BigInteger bilform(int[] a, int[] b){
     BigInteger[] u=new BigInteger[a.length];
     BigInteger[] v=new BigInteger[b.length];
     int i;
     for (i=0; i<a.length; i++){
         u[i]=BigInteger.valueOf(a[i]);
     }
     for (i=0; i<b.length; i++){
         v[i]=BigInteger.valueOf(b[i]);
     }
     return bilform(u,v);
 }
 
 public BigInteger bilform(BigInteger[] u, BigInteger[] v){
     BigInteger s=new BigInteger("0");
     int i,j;
     for (i=0; i<nbrows;i++){
         for (j=0;j<nbcols; j++){
             s=s.add(u[i].multiply(A[i][j].multiply(v[j])));
         }
     }
     return s;
 }
 
 public BigInteger weight(){
     BigInteger s=new BigInteger("0");
     int i,j;
     for (i=0; i<nbrows; i++){
         for (j=0; j<nbcols; j++){
             s=s.add(A[i][j].abs());
         }
     }
     return s;
 }
 
 public Boolean maxMultExceeds(Integer mm){
     BigInteger MaxMult=new BigInteger(mm.intValue()+"");
     int i,j;
     boolean exceeds=false;
     for (i=0; i<nbrows; i++){
         for (j=0; j<nbcols;j++){
             if (MaxMult.compareTo(A[i][j].abs())<0){
                 exceeds=true;
             }
         }
     }
     return new Boolean(exceeds);
 }
    
 public String toString(){
       int i,j;
       String s;
       int nber;
       
       s="";
       for(i=0; i<nbrows; i++){
       for(j=0; j<nbcols; j++){
           nber=A[i][j].intValue();
           s=s+" "+String.format("%5d", nber);
       } 
       s=s+"\n";};
       return s;
 }

 public String toSageMatrix(){
       int i,j;
       String s;

       s="matrix([";
       for(i=0; i<nbrows; i++){
           if (i>0){s=s+",";}
           s=s+"[";
           for(j=0; j<nbcols; j++){
              if (j>0){s=s+",";}
              s = s + A[i][j];
           }
           s=s+"]";
       }
       s=s+"]);";
       return s;
 }

 public String toMapleMatrix(){
       int i,j;
       String s;

       s="matrix("+nbrows+","+nbcols+",[";
       for(i=0; i<nbrows; i++){
           for(j=0; j<nbcols; j++){
              if ((i>0)||(j>0)){s=s+",";}
              s = s + A[i][j];
           }
       }
       s=s+"]);";
       return s;
 }
 
 public String colToString(int j){
     int i;
     String s;
     s="["+A[0][j];
     for (i=1;i<nbrows;i++){
         s=s+","+A[i][j];
     }
     s=s+"]";
     return s;
 }
 
 public String rowToVecString(int i){
     int j;
     String s;
     s="";
     for (j=0;j<nbcols;j++){
         int comp=A[i][j].compareTo(BigInteger.ZERO);
         if (comp!=0){
             if (comp>0){s=s+"+";} else {s=s+"-";}
             BigInteger N=A[i][j].abs();
             if (N.compareTo(BigInteger.ONE)!=0){s=s+N;}
             s=s+"["+(j+1)+"]";   
         }
     }
     return s;
 }
 
 public String colToVecString(int j){
     int i;
     String s;
     s="";
     for (i=0;i<nbrows;i++){
         int comp=A[i][j].compareTo(BigInteger.ZERO);
         if (comp!=0){
             if (comp>0){s=s+"+";} else {s=s+"-";}
             BigInteger N=A[i][j].abs();
             if (N.compareTo(BigInteger.ONE)!=0){s=s+N;}
             s=s+"["+(i+1)+"]";   
         }
     }
     return s;
 }
 
 public String rowToString(int i){
     int j;
     String s;
     s="["+A[i][0];
     for (j=1;j<nbcols;j++){
         s=s+","+A[i][j];
     }
     s=s+"]";
     return s;
 }
 
  

    public static BigInteger strangeprod(BigInteger p, BigInteger q) {
	
    if(p.signum()*q.signum()<0){
           BigInteger z=new BigInteger("0");
           return z;}
        else {
      if(p.signum()>0){return p.multiply(q);}
          else{
	      BigInteger m1= new BigInteger("-1");
	      return p.multiply(q).multiply(m1);}
    }
    }
    
    public void mutateViaCongruence(BMatrix B, int k){
        int n=nbrows;
        BMatrix E=IdentityMatrix(n);
        E.A[k][k]=new BigInteger("-1");
        for (int i=0;i<n;i++){
            if (i!=k){E.A[i][k]=B.A[i][k].max(BigInteger.ZERO);}
        }
        multiplyby(E);
        E.transpose();
        leftmultiplyby(E);
    }

    public void mutate(int mutindex ){
    int i,j,k; 

    k=mutindex;
    BigInteger[][] B= new BigInteger[nbrows][nbcols];
    for(i=0;i<nbrows; i++){
        for(j=0;j<nbcols;j++){
        if((i==k) || (j==k)){
	    BigInteger m1= new BigInteger("-1");
	    B[i][j]=A[i][j].multiply(m1);} 
         else  
            {B[i][j]=A[i][j].add(strangeprod(A[i][k],A[k][j]));
        }
        }
    }
    for(i=0;i<nbrows; i++){
        for(j=0;j<nbcols; j++){A[i][j]=null; A[i][j]=B[i][j];
        }
    }
    }
}


//public class MutationApplet extends JApplet {
public class MutationApp {
    public static final int APPLET=0;
    public static final int APPLICATION=1;
    public static final int WSAPPLICATION=2;
    
    public static int applType=APPLICATION;
    
    public MutationApp(){
        
    }
    
    //  public void init() {              
  public static void main(String[] args) {

        JLabel statusLabel= new JLabel("Status");
        
        JMenuItem showLabelsItem = new JMenuItem("Hide labels",
                         KeyEvent.VK_T);

        JMenuItem scaleCenterItem = new JMenuItem("Scale and center",
                         KeyEvent.VK_T);
        scaleCenterItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        
        

    JButton back = new JButton("Back");
    JButton forward=new JButton("Forward");
  
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           null, null);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(150);
    
    MutationApp ma=new MutationApp();
    QuiverDrawing qd = new QuiverDrawing(ma, statusLabel, showLabelsItem, back, forward, splitPane, applType);
    Dimension minimumSize = new Dimension(100, 100);
    qd.setMinimumSize(minimumSize);
    //pictureScrollPane.setMinimumSize(minimumSize);
   
    qd.setBackground(Color.white);
    qd.setPreferredSize(new Dimension(300, 450));
    
    splitPane.setTopComponent(qd);
    
    JFrame frame = new JFrame("Quiver mutation");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(splitPane);
    
    qd.setFrame(frame);
    frame.addWindowListener(qd);

    back.addActionListener(qd);

    forward.addActionListener(qd);
            
        JButton addvertices = new JButton("Add nodes");
        addvertices.addActionListener(qd);
        
        JButton addarrows = new JButton("Add arrows");
        addarrows.addActionListener(qd);

        JButton delnodes = new JButton("Delete nodes");
        delnodes.addActionListener(qd);
        
        JButton freezenodes = new JButton("Freeze nodes");
        freezenodes.addActionListener(qd);
        
        
        JButton done = new JButton("Done");
        done.addActionListener(qd);
        //qd.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "PressDone");
        //qd.getActionMap().put("PressDone", done.getAction());

        
        JToolBar toolBar = new JToolBar("Toolbar", BoxLayout.X_AXIS);
        toolBar.add(back);
	toolBar.add(forward);
        toolBar.add(addvertices);
        toolBar.add(addarrows);
        toolBar.add(delnodes);
        toolBar.add(freezenodes);
        //toolBar.add(art);
        toolBar.add(done);
        
        toolBar.setRollover(true);
        //toolBar.setFloatable(false);
       

    frame.getContentPane().add(toolBar, BorderLayout.NORTH);


    Box statusPanel = new Box(BoxLayout.X_AXIS);
    statusPanel.add(statusLabel);
    frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    //menu.setMnemonic(KeyEvent.VK_F);
    
    JMenuItem menuItem = new JMenuItem("New quiver ...",
                         KeyEvent.VK_T);
        /*menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_N, ActionEvent.ALT_MASK));*/
        //menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    if (applType!=APPLET){
        
        menuItem = new JMenuItem("Open ...");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_O, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_O);
            menuItem.addActionListener(qd);
            menu.add(menuItem);
        
        menuItem = new JMenuItem("Add ...");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_A, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_A);
            menuItem.addActionListener(qd);
            menu.add(menuItem);
            
        menuItem = new JMenuItem("Unite ...");
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_U, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_U);
            menuItem.addActionListener(qd);
            menu.add(menuItem);
            
        menuItem = new JMenuItem("Minus ...");
            menuItem.setMnemonic(KeyEvent.VK_U);
            menuItem.addActionListener(qd);
            menu.add(menuItem);    
                
            
        if (applType==APPLICATION){
            menuItem = new JMenuItem("Save as ...",
                             KeyEvent.VK_T);
            //menuItem.setMnemonic(KeyEvent.VK_A);
            menuItem.addActionListener(qd);
            menu.add(menuItem);
            
            menuItem = new JMenuItem("Save",
                             KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_S, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_S);
            menuItem.addActionListener(qd);
            menu.add(menuItem);
        }
        else {
        menuItem = new JMenuItem("Save ...",
                             KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_S, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_S);
            menuItem.addActionListener(qd);
        menu.add(menuItem);
        }
            
        menuItem = new JMenuItem("Export to xypic ...");
        menuItem.addActionListener(qd);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Export to Sage ...");
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
        menu.add(menuItem);

        menuItem = new JMenuItem("Export to Maple ...");
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
        menu.add(menuItem);
    
    

        menuItem = new JMenuItem("Print ...",
                             KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_P, ActionEvent.ALT_MASK));
            menuItem.setMnemonic(KeyEvent.VK_P);
            menuItem.addActionListener(qd);
        menu.add(menuItem);
    }
    
    menuItem = new JMenuItem("Reset");
        //menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuItem = new JMenuItem("Quit",
                         KeyEvent.VK_T);
            menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.addActionListener(qd);
    menu.add(menuItem);



    menuBar.add(menu);
    
    menu = new JMenu("View");
    //menu.setMnemonic(KeyEvent.VK_V);
    
    
    scaleCenterItem.addActionListener(qd);
    scaleCenterItem.setMnemonic(KeyEvent.VK_C);
    menu.add(scaleCenterItem);
    
    menuItem = new JMenuItem("Set boundary ...");
        menuItem.addActionListener(qd);
        menu.add(menuItem);
    
    
    menuItem = new JMenuItem("Move left");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(qd);
        menu.add(menuItem);
    
        
    
    
    menuItem = new JMenuItem("Hide frozen vertices");
        menuItem.addActionListener(qd);
        menu.add(menuItem);
        qd.setShowFrozenVerticesItem(menuItem);

    menuItem = new JMenuItem("Switch traffic lights on");
        menuItem.addActionListener(qd);
        menu.add(menuItem);
        qd.setTrafficLightsItem(menuItem);
    
    
     menu.addSeparator();
    
     menuItem = new JMenuItem("Color vertices ...");
        menuItem.addActionListener(qd);
        menu.add(menuItem);

    menuItem = new JMenuItem("Color all vertices ...");
        menuItem.addActionListener(qd);
        menu.add(menuItem);
    
    menuItem = new JMenuItem("Set radius ...");
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    showLabelsItem.addActionListener(qd);
    //showLabelsItem.setMnemonic(KeyEvent.VK_S);
    menu.add(showLabelsItem);
    
     menu.addSeparator();
    
    JMenu arrowStyleSubMenu=new JMenu("Arrow style");
        //activateSubMenu.setMnemonic(KeyEvent.VK_A);
        
    menuItem=new JMenuItem("invisible");
        menuItem.addActionListener(qd);
        arrowStyleSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("dashed");
        menuItem.addActionListener(qd);
        arrowStyleSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("dotted");
        menuItem.addActionListener(qd);
        arrowStyleSubMenu.add(menuItem);
    
    menuItem=new JMenuItem("standard");
        menuItem.addActionListener(qd);
        arrowStyleSubMenu.add(menuItem);
        
     menuItem=new JMenuItem("all standard");
        menuItem.addActionListener(qd);
        arrowStyleSubMenu.add(menuItem);
        
     menu.add(arrowStyleSubMenu);
     
    
    menuItem = new JMenuItem("Arrow labels ...");
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    JMenuItem shortnumbersItem = new JMenuItem("Short numbers");
    shortnumbersItem.addActionListener(qd);
    menu.add(shortnumbersItem);
    qd.setShortnumbersItem(shortnumbersItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Show grid");
    menuItem.addActionListener(qd);
    qd.setGridItem(menuItem);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Set grid size ...");
    menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuItem = new JMenuItem("Fit to grid");
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
   
    
    menu.addSeparator();
    
    JMenuItem lambdaQuiverItem = new JMenuItem("Lambda quiver");
    lambdaQuiverItem.addActionListener(qd);
    menu.add(lambdaQuiverItem);
    qd.setLambdaQuiverItem(lambdaQuiverItem);
    
    /*JMenuItem clearLambdaItem = new JMenuItem("Clear Lambda",
                         KeyEvent.VK_O);
    clearLambdaItem.addActionListener(qd);
    menu.add(clearLambdaItem);
    //qd.setLambdaQuiverItem(lambdaQuiverItem);
     */
    
    JMenuItem computeLambdaItem = new JMenuItem("Compute Lambda ...");
    computeLambdaItem.addActionListener(qd);
    menu.add(computeLambdaItem);
    //qd.setLambdaQuiverItem(lambdaQuiverItem);
    
    JMenuItem BtLItem = new JMenuItem("B^t * Lambda ...");
    BtLItem.addActionListener(qd);
    menu.add(BtLItem);
    
   
    
    menuBar.add(menu);
    
    menu = new JMenu("Tools");
    //menu.setMnemonic(KeyEvent.VK_T);
    
    menuItem = new JMenuItem("Add valued arrows",
                         KeyEvent.VK_V);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_V, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Antisymmetrizer ...");
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Matrix ...");
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Add framing");   
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Merge vertices");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);

    menu.addSeparator();
    
    menuItem = new JMenuItem("Renumber nodes",
                         KeyEvent.VK_V);
        //menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Oppose",
                         KeyEvent.VK_V);
        //menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Invert");
        //menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menu.addSeparator();

    menuItem = new JMenuItem("Show history ...",
                         KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_H, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Clear history",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    
    
    menuItem = new JMenuItem("Sequences ...",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuItem = new JMenuItem("Clear sequences",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Random ...",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Repeat random",
                         KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_R, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Look for ...",
                         KeyEvent.VK_H);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_L, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(qd);
    //menu.add(menuItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Show weight ...",
                         KeyEvent.VK_H);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_W, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_W);
        menuItem.addActionListener(qd);
    //menu.add(menuItem);
    
    menuItem = new JMenuItem("Make lighter ...",
                         KeyEvent.VK_H);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_M, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(qd);
    //menu.add(menuItem);
    
    menuItem = new JMenuItem("Repeat make lighter",
                         KeyEvent.VK_H);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_M, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(qd);
    //menu.add(menuItem);
    
    menuItem=new JMenuItem("Store/recall quiver");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Live quivers");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_X, ActionEvent.ALT_MASK));
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Parameters ...");
        //menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menu.addSeparator();
    
    

    menuItem = new JMenuItem("Mutation class ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("First quiver");
    menuItem.setMnemonic(KeyEvent.VK_F);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_F, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuItem = new JMenuItem("Previous quiver");
    menuItem.setMnemonic(KeyEvent.VK_E);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Next quiver");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Go to quiver ...");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_G, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_G);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Nb. mult. arr. ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Mult. arrows first");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Long hist. first");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Weight order");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
     
    
    menuItem = new JMenuItem("Clear");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Store");
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    //menu.add(menuItem);
    
    
    
    menuBar.add(menu);
     
    qd.toolMenu=menu;
    qd.updatetoolmenu();

    
    menu=new JMenu("Clusters");
    //menu.setMnemonic(KeyEvent.VK_C);
    qd.setClusterMenu(menu);
    
    
    menuItem=new JMenuItem("Set Cartan matrix");
        menuItem.addActionListener(qd);
        menuItem.setMnemonic(KeyEvent.VK_A);
        //menu.add(menuItem);
        qd.setCartanItem(menuItem);
         
    menuItem=new JMenuItem("Font size ...");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_F);
        menu.add(menuItem);
        qd.setFontSizeItem(menuItem);
        menuItem.setEnabled(false);
        
    menuItem=new JMenuItem("Show numbers");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_F);
        menu.add(menuItem);
        qd.setShowNumbersItem(menuItem);
        menuItem.setEnabled(false);

    menuItem=new JMenuItem("Show last X-mut. ...");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_F);
        menu.add(menuItem);
        
    menuItem=new JMenuItem("jvx output ...");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_F);
        //menu.add(menuItem);
        //menuItem.setEnabled(false);
        
    menuItem=new JMenuItem("Traverse ...");
        menuItem.addActionListener(qd);
        menuItem.setMnemonic(KeyEvent.VK_F);
        //menu.add(menuItem);
        
    JMenu activateSubMenu=new JMenu("Activate");
        //activateSubMenu.setMnemonic(KeyEvent.VK_A);
        
    menuItem=new JMenuItem("g-Tracker");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("h-Tracker");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("d-Tracker");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
    
    menuItem=new JMenuItem("QXF-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("F-polynomials");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("F'-polynomials");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("d-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
     
    menuItem=new JMenuItem("f-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);    
        
     menuItem=new JMenuItem("g-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
     menuItem=new JMenuItem("Guo-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
     menuItem=new JMenuItem("g-lincombs");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
     menuItem=new JMenuItem("g-matrix");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
     menuItem=new JMenuItem("gr-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
       
    menuItem=new JMenuItem("h-vectors");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("X-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("X-variables ...");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("QX-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("Num. X-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("Num. Xt-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("Y-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("Y-variables ...");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);
        
    menuItem=new JMenuItem("Yt-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem);    
        
    menuItem=new JMenuItem("XY-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        activateSubMenu.add(menuItem); 
        
    menuItem=new JMenuItem("KS-variables");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_A);
        //activateSubMenu.add(menuItem); 
        
    menuItem=new JMenuItem("Reset vars./dim.");
        menuItem.addActionListener(qd);
        //menuItem.setMnemonic(KeyEvent.VK_R);
        //menu.add(menuItem);
        //menuItem.setEnabled(false);
   
    menu.add(activateSubMenu);
        
        JMenu deactivateSubMenu=new JMenu("Deactivate");
        //deactivateSubMenu.setMnemonic(KeyEvent.VK_A);
        qd.setDeactivateMenu(deactivateSubMenu);
        
        menu.add(deactivateSubMenu);
        
    menuBar.add(menu);
    
    menu=new JMenu("Repetition");
    
    menuItem = new JMenuItem("Dimensions ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Clusters ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Category S ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Frieze ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Repeat frieze ...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_Z, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Trop. frieze ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
        

    menuItem = new JMenuItem("Repeat trop. frieze ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Repeat trop. frieze");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_D, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuItem = new JMenuItem("Cluster add. fct. ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Decompose ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("AR-arrows (Delta) ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("AR-arrows (preinj.) ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    
    menuItem = new JMenuItem("AR-arrows (postproj.) ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Highest weights ...");
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Central charge ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
        qd.setCentralChargeItem(menuItem);
   menu.add(menuItem);

    menuItem = new JMenuItem("Enter dim. vector ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
        qd.setSubmodulesItem(menuItem);
   menu.add(menuItem);

   menuItem = new JMenuItem("Enter submodules ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
        qd.setSubmodulesItem(menuItem);
   menu.add(menuItem);

   menuItem = new JMenuItem("Toggle spikes");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
  
   
    
   menuBar.add(menu);
    
   menu=new JMenu("Diverse");

   menuItem = new JMenuItem("Quantum monomial ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Swap memory");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Triangle product");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("CY-dimension ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Add frozen vertices");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   
   
   menuItem = new JMenuItem("Test1 ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_T, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Banff branches");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_B, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem=new JMenuItem("Done");
        menuItem.addActionListener(qd);
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
   menu.add(menuItem);
   

   
   
   menu.addSeparator();
   
   

   menuItem = new JMenuItem("Stretch");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   //menu.add(menuItem);
        
   menuItem = new JMenuItem("Enter double w-quiver");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Enter w-quiver");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);

   menuItem = new JMenuItem("Enumerate w-quivers");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Reduce graph mod frozen");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //KeyEvent.VK_T, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Random w-quiver");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);

   menuItem = new JMenuItem("Next random w-quiver");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_M, ActionEvent.ALT_MASK));
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(qd);
   menu.add(menuItem);

   
   
   menuItem = new JMenuItem("Next w-quiver");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("Set tau-data",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Show tau-data ...",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Nb. of tau-finite quivers ...",
                         KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    
    menuItem = new JMenuItem("tau");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_T, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   menuItem = new JMenuItem("tau inverse");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_I, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
   menu.add(menuItem);
   
   
   
     menu.addSeparator();
   
   menuItem = new JMenuItem("Metric quiver ...");
        //menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Enter cycle ...");
        //menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Forget metric");
        //menuItem.setMnemonic(KeyEvent.VK_F);
        menuItem.addActionListener(qd);
    menu.add(menuItem);
    
   
   
   menu.addSeparator();
   
   JMenu musicSubMenu=new JMenu("Music");
   
   JCheckBoxMenuItem cbmenuItem = new JCheckBoxMenuItem();
    
   cbmenuItem = new JCheckBoxMenuItem("Replay");
        cbmenuItem.addActionListener(qd);
        qd.setReplayItem(cbmenuItem);
    musicSubMenu.add(cbmenuItem);
    
    cbmenuItem = new JCheckBoxMenuItem("Random");
        cbmenuItem.addActionListener(qd);
        qd.setRandomItem(cbmenuItem);
    musicSubMenu.add(cbmenuItem);
    
      menuItem = new JMenuItem("Speed ...");
        menuItem.addActionListener(qd);
    musicSubMenu.add(menuItem);
    
     menuItem = new JMenuItem("Faster");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    musicSubMenu.add(menuItem);
    
     menuItem = new JMenuItem("Slower");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.addActionListener(qd);
    musicSubMenu.add(menuItem);
    
    
    cbmenuItem = new JCheckBoxMenuItem("Mut. sound");
        cbmenuItem.addActionListener(qd);
        qd.setMutSoundItem(cbmenuItem);
    musicSubMenu.add(cbmenuItem);
    
 
    cbmenuItem = new JCheckBoxMenuItem("Bkgr. music");
        cbmenuItem.addActionListener(qd);
        qd.setBkgrSoundItem(cbmenuItem);
    musicSubMenu.add(cbmenuItem);
    
    if (applType!=APPLET){
         menuItem = new JMenuItem("Choose mut. sound ...");
            menuItem.addActionListener(qd);
        musicSubMenu.add(menuItem);

        menuItem = new JMenuItem("Choose bkgr. music ...");
            menuItem.addActionListener(qd);
        musicSubMenu.add(menuItem);
    }
    
   menu.add(musicSubMenu);
    
   menu.addSeparator();
   
   JMenu polySubMenu=new JMenu("Polyhedra");
   
   menuItem = new JMenuItem("Dual d-polyhedron ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    polySubMenu.add(menuItem);
   
   menuItem = new JMenuItem("d-polyhedron ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    polySubMenu.add(menuItem);
    
    menuItem = new JMenuItem("d-poly. (unnormed) ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    polySubMenu.add(menuItem);
    
    menuItem = new JMenuItem("g-polyhedron ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    polySubMenu.add(menuItem);
    
    
    menuItem = new JMenuItem("Export to jvx ...",
                         KeyEvent.VK_J);
        menuItem.addActionListener(qd);
    polySubMenu.add(menuItem);

    
    menuItem = new JMenuItem("Show heights",
                         KeyEvent.VK_G);
    menuItem.addActionListener(qd);
    qd.setHeightItem(menuItem);
    polySubMenu.add(menuItem);
    
    menu.add(polySubMenu);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Hints ...");
    //menuItem.setMnemonic(KeyEvent.VK_B);
    menuItem.addActionListener(qd);
    menu.add(menuItem);
    
     
    menuItem = new JMenuItem("About ...");
        //menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.addActionListener(qd);
    menu.add(menuItem);

    menuBar.add(menu);
    
    frame.setJMenuBar(menuBar);

    frame.pack();
    frame.setVisible(true);
    

  }
}
