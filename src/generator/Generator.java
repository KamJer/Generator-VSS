package generator;

import java.awt.geom.Point2D;

import interpolation.Interpolation;

public class Generator {

	private static final double MNO = 22.5;

	private static final double CHANGE = 0.3;
	private static final double CHANGE2 = 0.15;
	private static final double CHANGE_ODP = 0.2;

	public static void main(String[] args) {
		Generator generator = new Generator();
		generator.generate(0.35, 0.50, 0.61, 120, 240);
	}

	public void generate(double pompingMax, double sMax1, double sMax2, double ev1, double ev2) {
		double[] pom = pomping(pompingMax);
		double[] pomOdp = pompingOdp(pompingMax);
		double i0 = calculateI0(ev1, ev2);
		
		int number = (int) Math.round(pompingMax / 0.05 + 1);

//		calculating lower value
		double d1 = MNO / ev1;
		double d2 = MNO / ev2;
//		Initializing initial points for interpolation
		Point2D.Double[] znanePunkty = new Point2D.Double[3];

		znanePunkty[0] = new Point2D.Double(0.02, 0);
		znanePunkty[1] = new Point2D.Double(pom[number - 5], sMax1 - d1);
		znanePunkty[2] = new Point2D.Double(pom[number - 3], sMax1);

//		initializing points for VSS for pierwotny
		Point2D.Double[] pierwotny = new Point2D.Double[number];
//		setting initial points
		pierwotny[0] = znanePunkty[0];
		pierwotny[number - 3 - 2] = znanePunkty[1];
		pierwotny[number - 3] = znanePunkty[2];
//		interpolating 
		for (int i = 0; i < pierwotny.length; i++) {
			if (pierwotny[i] == null) {
				if (i < 2) {
					pierwotny[i] = Interpolation.linearInterpolation(znanePunkty, pom[i]);
					pierwotny[i].y = Generator.round(pierwotny[i].y + pierwotny[i].y * Math.random() * CHANGE * 1 / number * i, 2);
				} else {
					pierwotny[i] = Interpolation.linearInterpolation(znanePunkty, pom[i]);
					pierwotny[i].y = Generator.round(pierwotny[i].y + pierwotny[i].y * Math.random() * CHANGE2 * 1 / number * i, 2);
				}
			}
		}

//		setting zero for the wtorny
		double a = 0.2;
		double b = 0.6;
		double zero = (Math.random() * (b - a) + a) * pierwotny[pierwotny.length - 1].y;

		Point2D.Double[] znanePunktyOdpuszczanie = new Point2D.Double[2];
		znanePunktyOdpuszczanie[0] = new Point2D.Double(0, zero);
		znanePunktyOdpuszczanie[1] = pierwotny[pierwotny.length - 1];
		Point2D.Double[] odp1 = new Point2D.Double[number / 2];

		odpuszczanie(znanePunktyOdpuszczanie, odp1, pomOdp, CHANGE_ODP, number);

//		Initializing initial points for interpolation
		znanePunkty[0] = new Point2D.Double(0, 0);
		znanePunkty[1] = new Point2D.Double(pom[number - 5], sMax2 - d2);
		znanePunkty[2] = new Point2D.Double(pom[number - 3], sMax2);

//		initializing points for VSS for wtorny
		Point2D.Double[] wtorny = new Point2D.Double[number];

		wtorny[0] = znanePunkty[0];
		wtorny[number - 5] = znanePunkty[1];
		wtorny[number - 3] = znanePunkty[2];

//		interpolating 
		for (int i = 0; i < wtorny.length; i++) {
			if (wtorny[i] == null) {
				if (i < 2) {
					wtorny[i] = Interpolation.linearInterpolation(znanePunkty, pom[i]);
					wtorny[i].y = Generator.round(wtorny[i].y + wtorny[i].y * Math.random() * CHANGE * 1 / number * i, 2);
				} else {
					wtorny[i] = Interpolation.linearInterpolation(znanePunkty, pom[i]);
					wtorny[i].y = Generator.round(wtorny[i].y + wtorny[i].y * Math.random() * CHANGE2 * 1 / number * i, 2);
				}
			}
		}

		zero = (Math.random() * (b - a) + a) * pierwotny[pierwotny.length - 1].y;

		znanePunktyOdpuszczanie[0] = new Point2D.Double(0, zero);
		znanePunktyOdpuszczanie[1] = wtorny[wtorny.length - 1];
		Point2D.Double[] odp2 = new Point2D.Double[number / 2];

		odpuszczanie(znanePunktyOdpuszczanie, odp2, pomOdp, CHANGE_ODP, number);

		for (int i = 0; i < pierwotny.length; i++) {
			System.out.println(pierwotny[i]);
		}
		System.out.println();
		for (int i = odp1.length - 1; i > 0; i--) {
			System.out.println(odp1[i]);
		}
		System.out.println();
		for (int i = 0; i < wtorny.length; i++) {
			System.out.println(wtorny[i]);
		}
		System.out.println();
		for (int i = odp2.length - 1; i >= 0; i--) {
			System.out.println(odp2[i]);
		}

		System.out.println();
		System.out.println(i0);
	}

	private void odpuszczanie(Point2D.Double[] odp, Point2D.Double[] odp1, double[] pomOdp, double changeOdp, int number) {
		for (int i = 0; i < odp1.length; i++) {
			odp1[i] = Interpolation.linearInterpolation(odp, pomOdp[i]);
			odp1[i].y = Generator.round(odp1[i].y - odp1[i].y * Math.random() * changeOdp * 1 / (number / 2) * i, 2);
		}
	}

	private double calculateI0(double ev1, double ev2) {
		return ev2 / ev1;
	}

	private static double[] pomping(double pompingMax) {
		double[] pomping = new double[(int) Math.round((pompingMax / 0.05) + 1)];
		pomping[0] = 0.02;
		pomping[1] = 0.05;
		for (int i = 2; i < pomping.length; i++) {
			pomping[i] = Generator.round(pomping[i - 1] + 0.05, 2); 
		}
		return pomping;
	}
	
	private static double[] pompingOdp(double pompingMax) {
		double[] pomping = new double[(int) Math.round((pompingMax / 0.1) + 1)];
		pomping[0] = 0;
		pomping[1] = 0.05;
		for (int i = 2; i < pomping.length; i++) {
			pomping[i] = Generator.round(pomping[i - 1] + 0.1, 2); 
		}
		return pomping;
	}
	
	private static double round(double number, int space) {
		number = number * (Math.pow(10, space));
		number = Math.round(number);
		number = number / (Math.pow(10, space));
		return number;
	}
}
