package Ver1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class toptest {
	public static int[] i;
	public static void main(String[] args) {
		try {
			i= new int[100];
			i[0] = 0;
			i[2] = 2;
			P.out(i[1]);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
