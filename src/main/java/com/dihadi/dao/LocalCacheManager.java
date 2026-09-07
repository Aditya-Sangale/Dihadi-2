package com.dihadi.dao;

import com.dihadi.model.JobApplication;
import com.dihadi.model.Notification;
import com.dihadi.model.Project;
import com.dihadi.model.WorkforceRequirement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Local persistent disk cache manager to seamlessly support offline operations
 * and protect against Google Cloud Firestore daily quota exhaustion.
 */
public class LocalCacheManager {
    private static final File CACHE_DIR = new File(System.getProperty("user.dir"), "dihadi_cache");
    private static volatile long quotaBackoffUntil = 0;

    static {
        if (!CACHE_DIR.exists()) {
            CACHE_DIR.mkdirs();
        }
    }

    public static boolean isQuotaExhausted() {
        return System.currentTimeMillis() < quotaBackoffUntil;
    }

    public static void markQuotaExhausted() {
        // Backoff for 10 minutes so Firestore quota is not hammered with failing requests
        quotaBackoffUntil = System.currentTimeMillis() + (10 * 60 * 1000);
        System.err.println("[Dihadi Offline Cache] Firestore daily quota limit reached. Running seamlessly in local cache mode.");
    }

    public static boolean isQuotaExhaustedException(Throwable t) {
        if (t == null) return false;
        String msg = t.getMessage();
        if (msg != null && (msg.contains("RESOURCE_EXHAUSTED") || msg.contains("Quota exceeded"))) {
            return true;
        }
        return isQuotaExhaustedException(t.getCause());
    }

    // ==================== PROJECTS CACHE ====================

    public static synchronized void saveProjects(Collection<Project> projects) {
        if (projects == null || projects.isEmpty()) return;
        try {
            JSONArray arr = new JSONArray();
            for (Project p : projects) {
                if (p == null) continue;
                JSONObject obj = new JSONObject();
                obj.put("projectId", p.getProjectId() != null ? p.getProjectId() : "");
                obj.put("projectName", p.getProjectName() != null ? p.getProjectName() : "");
                obj.put("contactName", p.getContactName() != null ? p.getContactName() : "");
                obj.put("mobile", p.getMobile() != null ? p.getMobile() : "");
                obj.put("alternateMobile", p.getAlternateMobile() != null ? p.getAlternateMobile() : "");
                obj.put("email", p.getEmail() != null ? p.getEmail() : "");
                obj.put("pincode", p.getPincode() != null ? p.getPincode() : "");
                obj.put("city", p.getCity() != null ? p.getCity() : "");
                obj.put("state", p.getState() != null ? p.getState() : "");
                obj.put("addressLine1", p.getAddressLine1() != null ? p.getAddressLine1() : "");
                obj.put("addressLine2", p.getAddressLine2() != null ? p.getAddressLine2() : "");
                obj.put("landmark", p.getLandmark() != null ? p.getLandmark() : "");
                obj.put("status", p.getStatus() != null ? p.getStatus() : "Active");
                JSONArray imgArr = new JSONArray();
                if (p.getImageUrls() != null) {
                    for (String u : p.getImageUrls()) imgArr.put(u);
                }
                obj.put("imageUrls", imgArr);
                arr.put(obj);
            }
            writeFile("projects.json", arr.toString(2));
        } catch (Exception e) {
            // Ignored for cache write
        }
    }

    public static synchronized List<Project> loadProjects() {
        List<Project> list = new ArrayList<>();
        try {
            String content = readFile("projects.json");
            if (content == null || content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Project p = new Project();
                p.setProjectId(o.optString("projectId", ""));
                p.setProjectName(o.optString("projectName", ""));
                p.setContactName(o.optString("contactName", ""));
                p.setMobile(o.optString("mobile", ""));
                p.setAlternateMobile(o.optString("alternateMobile", ""));
                p.setEmail(o.optString("email", ""));
                p.setPincode(o.optString("pincode", ""));
                p.setCity(o.optString("city", ""));
                p.setState(o.optString("state", ""));
                p.setAddressLine1(o.optString("addressLine1", ""));
                p.setAddressLine2(o.optString("addressLine2", ""));
                p.setLandmark(o.optString("landmark", ""));
                p.setStatus(o.optString("status", "Active"));
                JSONArray imgArr = o.optJSONArray("imageUrls");
                List<String> imgs = new ArrayList<>();
                if (imgArr != null) {
                    for (int j = 0; j < imgArr.length(); j++) imgs.add(imgArr.getString(j));
                }
                p.setImageUrls(imgs);
                if (!p.getProjectId().isBlank()) {
                    list.add(p);
                }
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ==================== APPLICATIONS CACHE ====================

    public static synchronized void saveApplications(Collection<JobApplication> apps) {
        if (apps == null || apps.isEmpty()) return;
        try {
            JSONArray arr = new JSONArray();
            for (JobApplication a : apps) {
                if (a == null) continue;
                JSONObject obj = new JSONObject();
                obj.put("applicationId", a.getApplicationId() != null ? a.getApplicationId() : "");
                obj.put("workerMobile", a.getWorkerMobile() != null ? a.getWorkerMobile() : "");
                obj.put("jobTitle", a.getJobTitle() != null ? a.getJobTitle() : "");
                obj.put("jobLocation", a.getJobLocation() != null ? a.getJobLocation() : "");
                obj.put("jobWage", a.getJobWage() != null ? a.getJobWage() : "");
                obj.put("status", a.getStatus() != null ? a.getStatus() : "Pending");
                obj.put("projectId", a.getProjectId() != null ? a.getProjectId() : "");
                obj.put("recruiterMobile", a.getRecruiterMobile() != null ? a.getRecruiterMobile() : "");
                obj.put("requirementId", a.getRequirementId() != null ? a.getRequirementId() : "");
                obj.put("workerName", a.getWorkerName() != null ? a.getWorkerName() : "");
                obj.put("timestamp", a.getTimestamp() != null ? a.getTimestamp().getTime() : System.currentTimeMillis());
                arr.put(obj);
            }
            writeFile("applications.json", arr.toString(2));
        } catch (Exception ignored) {}
    }

    public static synchronized List<JobApplication> loadApplications() {
        List<JobApplication> list = new ArrayList<>();
        try {
            String content = readFile("applications.json");
            if (content == null || content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                JobApplication a = new JobApplication();
                a.setApplicationId(o.optString("applicationId", ""));
                a.setWorkerMobile(o.optString("workerMobile", ""));
                a.setJobTitle(o.optString("jobTitle", ""));
                a.setJobLocation(o.optString("jobLocation", ""));
                a.setJobWage(o.optString("jobWage", ""));
                a.setStatus(o.optString("status", "Pending"));
                a.setProjectId(o.optString("projectId", ""));
                a.setRecruiterMobile(o.optString("recruiterMobile", ""));
                a.setRequirementId(o.optString("requirementId", ""));
                a.setWorkerName(o.optString("workerName", ""));
                long ts = o.optLong("timestamp", System.currentTimeMillis());
                a.setTimestamp(new Date(ts));
                if (!a.getApplicationId().isBlank()) {
                    list.add(a);
                }
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ==================== REQUIREMENTS CACHE ====================

    public static synchronized void saveRequirements(Collection<WorkforceRequirement> reqs) {
        if (reqs == null || reqs.isEmpty()) return;
        try {
            JSONArray arr = new JSONArray();
            for (WorkforceRequirement r : reqs) {
                if (r == null) continue;
                JSONObject obj = new JSONObject();
                obj.put("requirementId", r.getRequirementId() != null ? r.getRequirementId() : "");
                obj.put("projectId", r.getProjectId() != null ? r.getProjectId() : "");
                obj.put("priority", r.getPriority() != null ? r.getPriority() : "");
                obj.put("workerType", r.getWorkerType() != null ? r.getWorkerType() : "");
                obj.put("subSkill", r.getSubSkill() != null ? r.getSubSkill() : "");
                obj.put("quantity", r.getQuantity());
                obj.put("dailyWages", r.getDailyWages());
                obj.put("waterFacility", r.isWaterFacility());
                obj.put("electricityFacility", r.isElectricityFacility());
                obj.put("accommodationFacility", r.isAccommodationFacility());
                obj.put("transportationFacility", r.isTransportationFacility());
                arr.put(obj);
            }
            writeFile("requirements.json", arr.toString(2));
        } catch (Exception ignored) {}
    }

    public static synchronized List<WorkforceRequirement> loadRequirements() {
        List<WorkforceRequirement> list = new ArrayList<>();
        try {
            String content = readFile("requirements.json");
            if (content == null || content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                WorkforceRequirement r = new WorkforceRequirement();
                r.setRequirementId(o.optString("requirementId", ""));
                r.setProjectId(o.optString("projectId", ""));
                r.setPriority(o.optString("priority", ""));
                r.setWorkerType(o.optString("workerType", ""));
                r.setSubSkill(o.optString("subSkill", ""));
                r.setQuantity(o.optInt("quantity", 1));
                r.setDailyWages(o.optDouble("dailyWages", 0.0));
                r.setWaterFacility(o.optBoolean("waterFacility", false));
                r.setElectricityFacility(o.optBoolean("electricityFacility", false));
                r.setAccommodationFacility(o.optBoolean("accommodationFacility", false));
                r.setTransportationFacility(o.optBoolean("transportationFacility", false));
                if (!r.getRequirementId().isBlank()) {
                    list.add(r);
                }
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ==================== NOTIFICATIONS CACHE ====================

    public static synchronized void saveNotifications(Collection<Notification> notifs) {
        if (notifs == null || notifs.isEmpty()) return;
        try {
            JSONArray arr = new JSONArray();
            for (Notification n : notifs) {
                if (n == null) continue;
                JSONObject obj = new JSONObject();
                obj.put("notificationId", n.getNotificationId() != null ? n.getNotificationId() : "");
                obj.put("recipientId", n.getRecipientId() != null ? n.getRecipientId() : "");
                obj.put("recipientType", n.getRecipientType() != null ? n.getRecipientType() : "");
                obj.put("senderName", n.getSenderName() != null ? n.getSenderName() : "");
                obj.put("senderContact", n.getSenderContact() != null ? n.getSenderContact() : "");
                obj.put("title", n.getTitle() != null ? n.getTitle() : "");
                obj.put("message", n.getMessage() != null ? n.getMessage() : "");
                obj.put("type", n.getType() != null ? n.getType() : "");
                obj.put("projectId", n.getProjectId() != null ? n.getProjectId() : "");
                obj.put("projectName", n.getProjectName() != null ? n.getProjectName() : "");
                obj.put("jobRole", n.getJobRole() != null ? n.getJobRole() : "");
                obj.put("timestamp", n.getTimestamp() != null ? n.getTimestamp().getTime() : System.currentTimeMillis());
                obj.put("read", n.isRead());
                arr.put(obj);
            }
            writeFile("notifications.json", arr.toString(2));
        } catch (Exception ignored) {}
    }

    public static synchronized List<Notification> loadNotifications() {
        List<Notification> list = new ArrayList<>();
        try {
            String content = readFile("notifications.json");
            if (content == null || content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Notification n = new Notification();
                n.setNotificationId(o.optString("notificationId", ""));
                n.setRecipientId(o.optString("recipientId", ""));
                n.setRecipientType(o.optString("recipientType", ""));
                n.setSenderName(o.optString("senderName", ""));
                n.setSenderContact(o.optString("senderContact", ""));
                n.setTitle(o.optString("title", ""));
                n.setMessage(o.optString("message", ""));
                n.setType(o.optString("type", ""));
                n.setProjectId(o.optString("projectId", ""));
                n.setProjectName(o.optString("projectName", ""));
                n.setJobRole(o.optString("jobRole", ""));
                n.setTimestamp(new Date(o.optLong("timestamp", System.currentTimeMillis())));
                n.setRead(o.optBoolean("read", false));
                if (!n.getNotificationId().isBlank()) {
                    list.add(n);
                }
            }
        } catch (Exception ignored) {}
        return list;
    }

    // ==================== FILE UTILS ====================

    private static void writeFile(String filename, String content) {
        try {
            File f = new File(CACHE_DIR, filename);
            try (FileWriter fw = new FileWriter(f, StandardCharsets.UTF_8)) {
                fw.write(content);
            }
        } catch (Exception ignored) {}
    }

    private static String readFile(String filename) {
        try {
            File f = new File(CACHE_DIR, filename);
            if (!f.exists()) return null;
            try (FileReader fr = new FileReader(f, StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[4096];
                int r;
                while ((r = fr.read(buf)) != -1) {
                    sb.append(buf, 0, r);
                }
                return sb.toString();
            }
        } catch (Exception ignored) {
            return null;
        }
    }
}
