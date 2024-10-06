import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class ArticleIndex {
    private int n;
    private boolean hashKeys;
    private Map<String, List<String>> ngramTable;
    private Map<String, String> keyTable;

    public ArticleIndex(int n, boolean hashKeys) {
        this.n = n;
        this.hashKeys = hashKeys;
        this.ngramTable = new HashMap<>();
        this.keyTable = new HashMap<>();
    }

    public void addArticle(String s, String key) {
        Set<String> grams = getNGrams(s, n);
        String h = hash(key);
        keyTable.put(h, key);
        for (String g : grams) {
            ngramTable.computeIfAbsent(g, k -> new ArrayList<>()).add(h);
        }
    }

    public String[] findMatch(String s) {
        Set<String> grams = getNGrams(s, n);
        List<String> hits = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (String g : grams) {
            if (ngramTable.containsKey(g)) {
                List<String> found = ngramTable.get(g);
                hits.addAll(found);
                scores.addAll(Collections.nCopies(found.size(), 1.0 / found.size()));
            }
        }
        if (hits.isEmpty()) {
            return new String[]{null, "0"};
        }
        Map<String, Double> totals = new HashMap<>();
        for (int i = 0; i < hits.size(); i++) {
            totals.put(hits.get(i), totals.getOrDefault(hits.get(i), 0.0) + scores.get(i));
        }
        String maxKey = Collections.max(totals.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
        return new String[]{keyTable.get(maxKey), String.valueOf(totals.get(maxKey))};
    }

    private Set<String> getNGrams(String s, int n) {
        String[] words = s.split("\\s+");
        Set<String> grams = new HashSet<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            String gram = sb.toString().trim();
            if (hashKeys) {
                gram = hash(gram);
            }
            grams.add(gram);
        }
        return grams;
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public  String validateMatch(String s1, String s2) {
        Set<String> ng1 = getNGrams(s1, 5);
        Set<String> ng2 = getNGrams(s2, 5);
        int inter = ng1.stream().filter(ng2::contains).mapToInt(x -> 1).sum();
        double score1 = (double) inter / ng1.size();
        double score2 = (double) inter / ng2.size();
        // System.out.println(score1 + " " + score2);
        return "Matched with "+Integer.toOctalString(inter)+" score with intersecton of "+(score1)+" and "+score2+". Verdict is "+(score1>0.5 && score2>0.5?true:false);
        
    }


    // public static void main(String[] args) {
    //     ArticleIndex testIndex = new ArticleIndex(10, true);
    //     String s1 = "Five minutes.\n   That's roughly how long it took Jimmy Ryce, 9, to vanish Monday - within sight of his family's south Dade home, where his mother was waiting for him to return from school.\n   The last person to see Jimmy was his school bus driver, who dropped Jimmy off sometime after 3 p.m. at his usual stop a few blocks north of his house. Jimmy, who attends the gifted program at Naranja Elementary, is the only student assigned to that stop at the corner of Southwest 232nd Street and 162nd Avenue.\nThat worried his parents, said Jimmy's older brother, Ted. They had been pressuring the school system to let off their son at their driveway, because the bus drives past their house anyway.\n   \"I guess now they're going to do something about it,\" said Ted, 18, fighting tears.\n   For two days, Metro-Dade police have combed the Redlands area near the missing boy's home. Dozens of officers, aided by dogs and helicopters, swarmed the area. The FBI opened a kidnapping investigation and joined them, with 20 agents in the field interviewing family and neighbors.\n   Still, the case has not been ruled a kidnapping, and police say there are no leads.\n   \"We know that Jimmy would not get into a stranger's car,\" said Pat Ronemous, Jimmy's aunt. \"He would have run toward the house. He would have to have been forced into a car.\"\n   More than 100 neighbors, from parents to retirees to local business owners, gathered at an elementary school on Wednesday to help search the pastures and woods around the community.\n   Others passed out some of the 30,000 fliers that had been printed in English and Spanish, the cost covered by local printers and neighbors.\n   Brenda Fowler still waved leaflets bearing Jimmy's picture at passing motorists, even though it was growing dark and she had been there since 10:30 a.m. She stood near Jimmy's bus stop, where a homemade sign lettered on plywood had been posted: Please help find Jimmy. Please pray.\n   \"It could be your kid,\" Fowler said. \"It could be my kid.\"\n   Jimmy could have been anyone's kid. Neighbors described him as a happy, sweet child who loved playing chess and baseball.\n   Both Donald and Claudine Ryce, Jimmy's parents, are lawyers; Donald is in private practice and Claudine, who is not licensed to practice in Florida, helps her husband. Samuel James Ryce is the youngest of their three children.\n   While the couple remained secluded with police Wednesday evening, Donald Ryce issued a printed statement to the media camped in front of their ornate security gate, now festooned with yellow ribbons. \"We'd like to take this opportunity to thank everyone for their help and support,\" he wrote. \"We just ask them to focus on any information about Jimmy.\"\n   Police will continue their search today.\n   \"It's very disappointing that we don't have stronger leads that point in one direction - whether it be kidnapping or foul play,\" said Patrick Brickman, a Metro-Dade police spokesman. Police confess they are stumped by this case. Jimmy appears to be a normal boy from a normal family, they say.\n   The Florida Department of Health and Rehabilitative Services has no record of complaints against the boy's family, said Evelio Torres, administrative assistant for the HRS district office serving Dade and Monroe counties.\n   And no witnesses have come forward. Liz Mindermann pulled into her driveway near the bus stop about the same time Jimmy would have gotten off the school bus. She remembers a light drizzle, but nothing else.\n   \"All you can do is have faith and hope,\" she said. \"You can't think that he's dead. I won't think like that.\"\n   Anyone with information on Jimmy Ryce should call Metro-Dade police at 471-8477. He is 9 years old, weighs 70 pounds, is 4 feet, 8 inches tall, with brown hair and blue eyes. He was last seen wearing blue jean shorts, a white T-shirt, and black-and-white high-topped sneakers.Originally Published: September 14, 1995 at 12:35 p.m.He was last seen wearing blue jean shorts, a white T-shirt, and black-and-white high-topped sneakers.";
    //     String s2 = "Five minutes.\n\nThat\u2019s roughly how long it took Jimmy Ryce, 9, to vanish Monday \u2013 within sight of his family\u2019s south Dade home, where his mother was waiting for him to return from school.\n\nThe last person to see Jimmy was his school bus driver, who dropped Jimmy off sometime after 3 p.m. at his usual stop a few blocks north of his house. Jimmy, who attends the gifted program at Naranja Elementary, is the only student assigned to that stop at the corner of Southwest 232nd Street and 162nd Avenue.\n\nThat worried his parents, said Jimmy\u2019s older brother, Ted. They had been pressuring the school system to let off their son at their driveway, because the bus drives past their house anyway.\n\n\u201cI guess now they\u2019re going to do something about it,\u201d said Ted, 18, fighting tears.\n\nFor two days, Metro-Dade police have combed the Redlands area near the missing boy\u2019s home. Dozens of officers, aided by dogs and helicopters, swarmed the area. The FBI opened a kidnapping investigation and joined them, with 20 agents in the field interviewing family and neighbors.\n\nStill, the case has not been ruled a kidnapping, and police say there are no leads.\n\n\u201cWe know that Jimmy would not get into a stranger\u2019s car,\u201d said Pat Ronemous, Jimmy\u2019s aunt. \u201cHe would have run toward the house. He would have to have been forced into a car.\u201d\n\nMore than 100 neighbors, from parents to retirees to local business owners, gathered at an elementary school on Wednesday to help search the pastures and woods around the community.\n\nOthers passed out some of the 30,000 fliers that had been printed in English and Spanish, the cost covered by local printers and neighbors.\n\nBrenda Fowler still waved leaflets bearing Jimmy\u2019s picture at passing motorists, even though it was growing dark and she had been there since 10:30 a.m. She stood near Jimmy\u2019s bus stop, where a homemade sign lettered on plywood had been posted: Please help find Jimmy. Please pray.\n\n\u201cIt could be your kid,\u201d Fowler said. \u201cIt could be my kid.\u201d\n\nJimmy could have been anyone\u2019s kid. Neighbors described him as a happy, sweet child who loved playing chess and baseball.\n\nBoth Donald and Claudine Ryce, Jimmy\u2019s parents, are lawyers; Donald is in private practice and Claudine, who is not licensed to practice in Florida, helps her husband. Samuel James Ryce is the youngest of their three children.\n\nWhile the couple remained secluded with police Wednesday evening, Donald Ryce issued a printed statement to the media camped in front of their ornate security gate, now festooned with yellow ribbons. \u201cWe\u2019d like to take this opportunity to thank everyone for their help and support,\u201d he wrote. \u201cWe just ask them to focus on any information about Jimmy.\u201d\n\nPolice will continue their search today.\n\n\u201cIt\u2019s very disappointing that we don\u2019t have stronger leads that point in one direction \u2013 whether it be kidnapping or foul play,\u201d said Patrick Brickman, a Metro-Dade police spokesman. Police confess they are stumped by this case. Jimmy appears to be a normal boy from a normal family, they say.\n\nThe Florida Department of Health and Rehabilitative Services has no record of complaints against the boy\u2019s family, said Evelio Torres, administrative assistant for the HRS district office serving Dade and Monroe counties.\n\nAnd no witnesses have come forward. Liz Mindermann pulled into her driveway near the bus stop about the same time Jimmy would have gotten off the school bus. She remembers a light drizzle, but nothing else.\n\n\u201cAll you can do is have faith and hope,\u201d she said. \u201cYou can\u2019t think that he\u2019s dead. I won\u2019t think like that.\u201d\n\nAnyone with information on Jimmy Ryce should call Metro-Dade police at 471-8477. He is 9 years old, weighs 70 pounds, is 4 feet, 8 inches tall, with brown hair and blue eyes. He was last seen wearing blue jean shorts, a white T-shirt, and black-and-white high-topped sneakers.\n\nOriginally Published: September 14, 1995 at 12:35 p.m.";
    //     String s3 = "Getting your house back to normal can help you get back to normal. But remember that damaged homes can pose danger. Take your time. Getting injured or making a bad decision because of haste will make a difficult situation worse.\n\nGeneral\n\nIf your home looks unsafe, it probably is. Emergency management officials have procedures to certify structures for safety after a hurricane, and it is wise when possible to wait for them.\n\nAssessing damage on your own requires the right gear, including dry, rubber-soled shoes; rubber gloves or work gloves; hammer; screwdriver; pencil; and note paper. Do not inspect anything at night. Wait until daylight, and even then use a good flashlight when you go inside.\n\nMake only temporary repairs necessary to prevent further damage. If you can, photograph the damage before you make stopgap repairs, and keep all receipts. Don\u2019t make permanent repairs until your insurance agent inspects the property.\n\nInside\n\nBe careful when entering and moving around in a damaged home. Do not smoke or use an open flame. If you smell gas, turn it off at the meter or tank, or call your gas company.\n\nWatch for loose electrical wires and ceilings, beams and other objects that could fall. Never touch an electrical appliance, any wiring or a tool while standing in water. Be careful not to further weaken your home while removing debris.\n\nOpen all doors and windows to release moisture, odors and dangerous gases. If you cannot get a window open, use your tools to remove the sash. If a door won\u2019t open, remove the hinge pins and take off the entire door.\n\nWhile you\u2019re inside, look skyward to detect holes where water can get in through the roof. Make note of them so you know where to make exterior repairs.\n\nThe roof\n\nFrom the outside, inspect roof supports, ridge areas, gable ends and eaves. The roof may have stayed intact but shifted. Such problems require professional repair.\n\nIf you go into the attic, step only on wooden roof supports. If you step elsewhere, you could fall through the ceiling. In the attic, cracked roof supports can be repaired temporarily by running 8-foot (or longer) 2-by-4s on each side of a broken support and nailing them to the cracked support.\n\nUse extreme caution and wear rubber-soled shoes if you decide to step onto the roof. Do not walk around; roofs that appear intact could have been weakened during a hurricane.\n\nLook for missing asphalt roof shingles and missing or broken roof tiles. On flat roofs, look for areas where the gravel surface and the underlying foundation has been torn away.\n\nEmergency repairs to leaky roofs can be made in a variety of ways. \u201cSneaky paper\u201d comes in rolls and has a self-adhesive side that sticks to the roof. Plastic sheeting at least 6 millimeters thick can also stop leaks.";
    //     String s4 = "\nWhen getting your damaged house back to normal, take your time. Getting injured or making a bad decision out of haste will make a difficult situation worse. Here are some tips on how to keep you and your family safe:\n-- If your home looks unsafe, it probably is unsafe. Emergency-management officials have plans to certify structures for safety after a hurricane, and it is wise to wait for them.\n-- Assessing damage on your own requires the right gear, including dry, rubber-soled shoes; rubber gloves or work gloves; hammer; screwdriver; pencil; and note paper.\n-- Walk slowly around your home, looking for big problems such as whether your home shifted on its slab. Your roof, even if it looks intact, may have shifted, too. Such problems require professional repair.\n-- As you go inside the house, open all doors and windows to release moisture, odors and dangerous gases. If you smell gas, turn it off at the meter or tank. Do not smoke or use an open flame.\n-- Beware of loose wires and sagging walls, roofs and ceilings. Be careful not to further weaken your home while removing debris.\n-- Never touch an electrical appliance or tool while standing in a pool of water.\n-- Brace walls where necessary with 2-by-4 studs.\n-- If the walls of a wood-frame home are waterlogged, drill or punch \"weep holes\" in interior walls to let water out and speed the drying process.\n-- If you have wood floors that buckled, don't try to straighten them until they've dried. Then, take up the flooring and fasten it back down evenly. It can be a difficult, exacting job, and some flooring may need to be replaced altogether; you may need a professional. If you have ceramic or terrazzo tile on top of concrete flooring, let the floor dry, then reattach any loose tiles with appropriate cement or fastener. This job, too, may be best left to a professional.\n-- Remember, make only temporary repairs necessary to prevent further damage. If you can, photograph the damage before you make stopgap repairs, and keep all receipts. Don't make permanent repairs until your insurance agent inspects the property.\nRATING THE ROOF\n-- Do not inspect anything at night. Wait until daylight, and even then have a good flashlight handy.\n-- From inside the house, look skyward to detect holes where water can get in. Make note of those areas, so you know where to make exterior repairs.\n-- Use extreme caution - and wear rubber-soled shoes - if you decide to step onto the roof. Do not walk around up there any more than you must; roofs that appear intact could have been weakened during a hurricane.\n-- Look for missing asphalt roof shingles, and missing or broken roof tiles. On flat roofs, look for areas where the gravel surface and underlayment has been torn away. Inspect roof supports, ridge areas, gable ends and eaves.\n-- In the attic, step only on wooden roof supports. If you step elsewhere, you could fall through the ceiling. Watch out for live electrical wires, jagged wood.\n-- Broken roof supports in the attic can be repaired temporarily by running 8-foot (or longer) 2-by-4's on each side of a broken support, and nailing them to the broken support.\n-- Protect your home from further damage by fixing roof leaks, or hiring someone to make emergency repairs. Emergency repairs to leaky roofs can be made with \"sneaky paper,\" which comes in rolls and has a self-adhesive side that sticks to the roof. Plastic sheeting, such as Visqueen, can also be used to stop leaks. Plastic sheeting should be at least 6 mils thick.\n-- Roofing paper is applied by alternating layers of trowel-grade roof cement and paper. Apply layers from the lowest part of the roof to the top.\n-- Remember to keep receipts for everything you buy; that will help ensure that your insurance claim gets paid.";
        
    //     System.out.println(testIndex.validateMatch(s3,s4));
        
    
    // }
        
}