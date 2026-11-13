<template>
  <div class="container">
    <h1>Hello World! 🎉</h1>
    <p>Congratulations, you have logged in successfully!</p>

    <button @click="goBack">Return to login page</button>
    <button @click="openCourseApi">Open Course API</button>
    <button @click="fetchCourses">Load Courses</button>

    <!-- Loading and error messages -->
    <p v-if="loading">Loading...</p>
    <p v-if="statusMessage" class="error">{{ statusMessage }}</p>

    <!-- Heading only shows after the user clicks Load Courses -->
    <h2 v-if="hasSearched">Course List</h2>

    <!-- Course Table -->
    <table v-if="courses.length > 0" class="course-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Course Code</th>
          <th>Course Title</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="course in courses" :key="course.id">
          <td>{{ course.id }}</td>
          <td>{{ course.code }}</td>
          <td>{{ course.title }}</td>
        </tr>
      </tbody>
    </table>

    <!-- Empty State -->
    <div v-else-if="hasSearched">
      <p>No courses found in the database.</p>
    </div>

    <!-- Initial State -->
    <div v-else>
      <p>Click the button to load courses.</p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";

const router = useRouter();

// Reactive state variables
const courses = ref([]);
const loading = ref(false);
const statusMessage = ref("");
const hasSearched = ref(false);

const goBack = () => {
  localStorage.clear();
  router.push("/");
};

const openCourseApi = () => {
  window.open("http://localhost:8080/api/courses", "_blank");
};

const fetchCourses = async () => {
  loading.value = true;
  statusMessage.value = "";

  try {
    const response = await fetch("http://localhost:8080/api/courses");

    // 204 means the database has no courses
    if (response.status === 204) {
      courses.value = [];
      hasSearched.value = true;
      return;
    }

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json();
    courses.value = data;
    hasSearched.value = true;
  } catch (error) {
    console.error("Error fetching courses:", error);
    statusMessage.value =
      "Failed to load courses. Please check if the backend is running.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.container {
  margin: 20px;
  text-align: center;
}

button {
  margin: 15px 5px 0 5px;
  padding: 8px 16px;
  cursor: pointer;
}

.error {
  color: red;
}

/* Table design */
.course-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

.course-table th,
.course-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}

.course-table th {
  background-color: #2c7a4b;
  color: white;
}

.course-table tr:nth-child(even) {
  background-color: #f2f2f2;
}

.course-table tr:hover {
  background-color: #d9f0e3;
}
</style>
